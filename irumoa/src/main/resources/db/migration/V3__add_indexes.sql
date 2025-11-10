-- 역조회 인덱스 (PK와 별개, 조회용)

CREATE INDEX idx_program_category_category
  ON program_category(category);
  
CREATE INDEX idx_program_department_department
  ON program_department(department);

CREATE INDEX idx_program_grade_grade
  ON program_grade(grade);
