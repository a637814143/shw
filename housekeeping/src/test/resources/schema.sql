DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `real_name` VARCHAR(50),
    `phone` VARCHAR(20) UNIQUE,
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `gender` TINYINT,
    `birthday` DATE,
    `address` VARCHAR(255),
    `balance` DECIMAL(10,2) DEFAULT 0.00,
    `status` TINYINT DEFAULT 1,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS `service_provider`;
CREATE TABLE `service_provider` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `real_name` VARCHAR(50),
    `phone` VARCHAR(20) UNIQUE,
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `gender` TINYINT,
    `birthday` DATE,
    `address` VARCHAR(255),
    `id_card` VARCHAR(18) UNIQUE,
    `work_experience` TEXT,
    `skills` TEXT,
    `certification_status` TINYINT DEFAULT 0,
    `certification_time` TIMESTAMP,
    `balance` DECIMAL(10,2) DEFAULT 0.00,
    `status` TINYINT DEFAULT 1,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `real_name` VARCHAR(50),
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `status` TINYINT DEFAULT 1,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
