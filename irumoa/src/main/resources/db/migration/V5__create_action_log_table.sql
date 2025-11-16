CREATE TABLE action_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  program_id BIGINT NOT NULL,
  department TEXT NOT NULL,
  grade INT NOT NULL,
  interests TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_al_program FOREIGN KEY (program_id)
    REFERENCES program(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

