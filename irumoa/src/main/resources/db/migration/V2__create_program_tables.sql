CREATE TABLE program (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  link VARCHAR(500) NOT NULL,
  content TEXT NOT NULL,
  app_start_date DATE NULL,
  app_end_date DATE NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE program_category (
  program_id BIGINT NOT NULL,
  category VARCHAR(100) NOT NULL,
  PRIMARY KEY (program_id, category),
  CONSTRAINT fk_pc_program FOREIGN KEY (program_id)
    REFERENCES program(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE program_department (
  program_id BIGINT NOT NULL,
  department VARCHAR(200) NOT NULL,
  PRIMARY KEY (program_id, department),
  CONSTRAINT fk_pd_program FOREIGN KEY (program_id)
    REFERENCES program(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE program_grade (
  program_id BIGINT NOT NULL,
  grade INT NOT NULL,
  PRIMARY KEY (program_id, grade),
  CONSTRAINT fk_pg_program FOREIGN KEY (program_id)
    REFERENCES program(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
