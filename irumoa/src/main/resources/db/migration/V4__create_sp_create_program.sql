DROP PROCEDURE IF EXISTS sp_create_program;
DELIMITER $$

CREATE PROCEDURE sp_create_program(IN p JSON, OUT out_program_id BIGINT)
BEGIN
  DECLARE v_app_start VARCHAR(20);
  DECLARE v_app_end   VARCHAR(20);
  DECLARE v_pid BIGINT;

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  IF JSON_CONTAINS_PATH(p, 'all',
        '$.title','$.category','$.link','$.content') = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'missing required fields';
  END IF;

  SET v_app_start = JSON_UNQUOTE(JSON_EXTRACT(p,'$.app_start_date'));
  SET v_app_end   = JSON_UNQUOTE(JSON_EXTRACT(p,'$.app_end_date'));

  START TRANSACTION;

  INSERT INTO program (title, category, link, content, app_start_date, app_end_date)
  VALUES (
    JSON_UNQUOTE(JSON_EXTRACT(p,'$.title')),
    JSON_UNQUOTE(JSON_EXTRACT(p,'$.category')),
    JSON_UNQUOTE(JSON_EXTRACT(p,'$.link')),
    JSON_UNQUOTE(JSON_EXTRACT(p,'$.content')),
    CAST(NULLIF(v_app_start,'') AS DATE),
    CAST(NULLIF(v_app_end,'')   AS DATE)
  );

  SET v_pid = LAST_INSERT_ID();

  INSERT IGNORE INTO program_department (program_id, department)
  SELECT v_pid, jt.dept
  FROM JSON_TABLE(
         COALESCE(JSON_EXTRACT(p,'$.departments'),'[]'),
         '$[*]' COLUMNS (dept VARCHAR(200) PATH '$')
       ) jt
  WHERE jt.dept IS NOT NULL
  GROUP BY jt.dept;

  -- 디버그: 프로시저가 실제로 본 grades와 타입
  SELECT JSON_EXTRACT(p,'$.grades')   AS dbg_grades,
         JSON_TYPE(JSON_EXTRACT(p,'$.grades')) AS dbg_type;

  -- 여기서 바로 리턴해보면, 에러가 아래 블록에서 나는지 확정됨
  -- LEAVE 대신 RETURN 효과: OUT 파라미터만 셋팅해서 나가기
  SET out_program_id = v_pid;
  -- 주석 해제해서 한 번 실행해보세요.
  -- LEAVE_PROC: 
  -- LEAVE_PROC END;

  -- grades 블록(지금은 아직 기존)
  INSERT IGNORE INTO program_grade (program_id, grade)
  SELECT v_pid, CAST(jt.g AS UNSIGNED)
  FROM JSON_TABLE(
         COALESCE(JSON_EXTRACT(p,'$.grades'),'[]'),
         '$[*]' COLUMNS (g VARCHAR(32) PATH '$')
       ) jt
  WHERE jt.g REGEXP '^[0-9]+$'
  GROUP BY jt.g;

  COMMIT;
  SET out_program_id = v_pid;
END$$
DELIMITER ;
