DROP PROCEDURE IF EXISTS sp_create_program;
DELIMITER $$

CREATE PROCEDURE sp_create_program(
    IN p JSON,
    OUT out_program_id BIGINT
)
BEGIN
    DECLARE v_title VARCHAR(255);
    DECLARE v_link VARCHAR(500);
    DECLARE v_content TEXT;
    DECLARE v_app_start DATE;
    DECLARE v_app_end DATE;

    DECLARE v_program_id BIGINT;

    DECLARE v_cat_len INT DEFAULT 0;
    DECLARE v_dep_len INT DEFAULT 0;
    DECLARE v_grade_len INT DEFAULT 0;

    DECLARE i INT DEFAULT 0;

    -- 에러시 롤백
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    -- 필수값 체크
    IF JSON_CONTAINS_PATH(p, 'all',
           '$.title', '$.link', '$.content') = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'missing required fields';
    END IF;

    SET v_title   = JSON_UNQUOTE(JSON_EXTRACT(p, '$.title'));
    SET v_link    = JSON_UNQUOTE(JSON_EXTRACT(p, '$.link'));
    SET v_content = JSON_UNQUOTE(JSON_EXTRACT(p, '$.content'));

    SET v_app_start = NULL;
    SET v_app_end   = NULL;

    IF JSON_CONTAINS_PATH(p, 'one', '$.app_start_date') = 1 THEN
        SET v_app_start = STR_TO_DATE(
            JSON_UNQUOTE(JSON_EXTRACT(p, '$.app_start_date')),
            '%Y-%m-%d'
        );
    END IF;

    IF JSON_CONTAINS_PATH(p, 'one', '$.app_end_date') = 1 THEN
        SET v_app_end = STR_TO_DATE(
            JSON_UNQUOTE(JSON_EXTRACT(p, '$.app_end_date')),
            '%Y-%m-%d'
        );
    END IF;

    START TRANSACTION;

    -- 1. program insert
    INSERT INTO program (title, link, content, app_start_date, app_end_date)
    VALUES (v_title, v_link, v_content, v_app_start, v_app_end);

    SET v_program_id = LAST_INSERT_ID();

    -- 2. categories 배열 처리
    IF JSON_CONTAINS_PATH(p, 'one', '$.categories') = 1 THEN
        SET v_cat_len = JSON_LENGTH(JSON_EXTRACT(p, '$.categories'));
        SET i = 0;
        WHILE i < v_cat_len DO
            INSERT INTO program_category (program_id, category)
            VALUES (
                v_program_id,
                JSON_UNQUOTE(
                    JSON_EXTRACT(p, CONCAT('$.categories[', i, ']'))
                )
            );
            SET i = i + 1;
        END WHILE;
    END IF;

    -- 3. departments 배열 처리
    IF JSON_CONTAINS_PATH(p, 'one', '$.departments') = 1 THEN
        SET v_dep_len = JSON_LENGTH(JSON_EXTRACT(p, '$.departments'));
        SET i = 0;
        WHILE i < v_dep_len DO
            INSERT INTO program_department (program_id, department)
            VALUES (
                v_program_id,
                JSON_UNQUOTE(
                    JSON_EXTRACT(p, CONCAT('$.departments[', i, ']'))
                )
            );
            SET i = i + 1;
        END WHILE;
    END IF;

    -- 4. grades 배열 처리
    IF JSON_CONTAINS_PATH(p, 'one', '$.grades') = 1 THEN
        SET v_grade_len = JSON_LENGTH(JSON_EXTRACT(p, '$.grades'));
        SET i = 0;
        WHILE i < v_grade_len DO
            INSERT INTO program_grade (program_id, grade)
            VALUES (
                v_program_id,
                CAST(
                    JSON_UNQUOTE(
                        JSON_EXTRACT(p, CONCAT('$.grades[', i, ']'))
                    ) AS SIGNED
                )
            );
            SET i = i + 1;
        END WHILE;
    END IF;

    COMMIT;

    SET out_program_id = v_program_id;
END$$

DELIMITER ;
