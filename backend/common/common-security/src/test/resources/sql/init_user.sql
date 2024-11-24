-- 清空用户表
DELETE FROM user;

-- 插入测试用户数据
INSERT INTO user (id, username, password, enabled, roles) 
VALUES (1, 'testuser', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqV90IhHZJ8g4YYyGCJYwLW5kGrG', true, 'ROLE_USER');
