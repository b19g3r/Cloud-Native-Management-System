DROP TABLE IF EXISTS user;

CREATE TABLE user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  roles VARCHAR(255),
  CONSTRAINT uk_username UNIQUE (username)
);
