CREATE DATABASE IF NOT EXISTS educloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE educloud;

-- 用户表（先最简，后面再加角色/权限等）
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
                          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                          `username` VARCHAR(64) NOT NULL UNIQUE,
                          `password` VARCHAR(128) NOT NULL,
                          `role` VARCHAR(32) NOT NULL DEFAULT 'USER',
                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 课程表
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
                            `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                            `title` VARCHAR(255) NOT NULL,
                            `price` INT NOT NULL DEFAULT 0,     -- 单位：分
                            `stock` INT NOT NULL DEFAULT 0,
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
                           `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                           `user_id` BIGINT NOT NULL,
                           `course_id` BIGINT NOT NULL,
                           `amount` INT NOT NULL DEFAULT 0,    -- 单位：分
                           `status` VARCHAR(16) NOT NULL DEFAULT 'NEW',  -- NEW/PAID/CLOSED
                           `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           INDEX idx_user_id (`user_id`),
                           INDEX idx_course_id (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化数据（可选）
INSERT INTO t_user(username, password, role) VALUES ('admin','123456','ADMIN');
INSERT INTO t_course(title, price, stock) VALUES ('Java基础入门', 19900, 100);
INSERT INTO t_course(title, price, stock) VALUES ('Spring Cloud 实战', 29900, 50);
