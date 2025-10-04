/*
 家政服务管理系统数据库表结构
 创建时间: 2024-12-19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS housekeeping_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE housekeeping_db;

-- 系统角色账号表
DROP TABLE IF EXISTS `user_family`;
CREATE TABLE `user_family` (
  `user_name` varchar(100) NOT NULL COMMENT '登录账号',
  `password` varchar(128) NOT NULL COMMENT '密码（Base64后SHA-512）',
  `types` varchar(20) NOT NULL COMMENT '角色类型：ADMIN/USER/PROVIDER',
  `money` decimal(12,2) NOT NULL DEFAULT 0 COMMENT '钱包余额',
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色账号表';

-- 管理员表
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 普通用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='普通用户表';

-- 家政服务者表
DROP TABLE IF EXISTS `service_provider`;
CREATE TABLE `service_provider` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务者ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `work_experience` text COMMENT '工作经验',
  `skills` text COMMENT '技能特长',
  `certification_status` tinyint DEFAULT '0' COMMENT '认证状态：0-未认证，1-已认证，2-认证失败',
  `certification_time` datetime DEFAULT NULL COMMENT '认证时间',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务者表';

-- 服务分类表
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` text COMMENT '分类描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务分类表';

-- 家政服务表
DROP TABLE IF EXISTS `housekeeping_service`;
CREATE TABLE `housekeeping_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `name` varchar(100) NOT NULL COMMENT '服务名称',
  `description` text COMMENT '服务描述',
  `content` longtext COMMENT '服务详情（富文本）',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `price` decimal(10,2) NOT NULL COMMENT '服务价格',
  `unit` varchar(20) DEFAULT '次' COMMENT '服务单位',
  `duration` int DEFAULT NULL COMMENT '服务时长（分钟）',
  `images` text COMMENT '服务图片（JSON数组）',
  `tags` varchar(255) DEFAULT NULL COMMENT '服务标签',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分',
  `review_count` int DEFAULT '0' COMMENT '评价数量',
  `booking_count` int DEFAULT '0' COMMENT '预约数量',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_service_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`),
  CONSTRAINT `fk_service_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务表';

-- 服务预约表
DROP TABLE IF EXISTS `service_booking`;
CREATE TABLE `service_booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `booking_no` varchar(32) NOT NULL COMMENT '预约单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `booking_date` date NOT NULL COMMENT '预约日期',
  `booking_time` time NOT NULL COMMENT '预约时间',
  `address` varchar(255) NOT NULL COMMENT '服务地址',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人',
  `remark` text COMMENT '备注',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待分配，1-已分配，2-进行中，3-已完成，4-已取消',
  `payment_status` tinyint DEFAULT '0' COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_no` (`booking_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_booking_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`),
  CONSTRAINT `fk_booking_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';

-- 服务评价表
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `booking_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片（JSON数组）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_id` (`booking_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_review_booking` FOREIGN KEY (`booking_id`) REFERENCES `service_booking` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`),
  CONSTRAINT `fk_review_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_service` (`user_id`, `service_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_favorite_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 充值记录表
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '充值ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(64) DEFAULT NULL COMMENT '交易单号',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待支付，1-已支付，2-支付失败',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_recharge_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- 提现记录表
DROP TABLE IF EXISTS `withdraw_record`;
CREATE TABLE `withdraw_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提现ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `account_holder` varchar(50) DEFAULT NULL COMMENT '开户人',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_withdraw_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- 轮播图表
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) DEFAULT NULL COMMENT '链接地址',
  `service_id` bigint DEFAULT NULL COMMENT '关联服务ID',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  CONSTRAINT `fk_banner_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 居家贴士表
DROP TABLE IF EXISTS `housekeeping_tip`;
CREATE TABLE `housekeeping_tip` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '贴士ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '内容（富文本）',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-草稿，1-发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='居家贴士表';

-- 系统公告表
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '内容（富文本）',
  `type` tinyint DEFAULT '1' COMMENT '类型：1-系统公告，2-活动通知',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-草稿，1-发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- 服务者认证材料表
DROP TABLE IF EXISTS `provider_certification`;
CREATE TABLE `provider_certification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `id_card_front` varchar(255) DEFAULT NULL COMMENT '身份证正面',
  `id_card_back` varchar(255) DEFAULT NULL COMMENT '身份证反面',
  `health_certificate` varchar(255) DEFAULT NULL COMMENT '健康证',
  `skill_certificate` varchar(255) DEFAULT NULL COMMENT '技能证书',
  `work_experience` text COMMENT '工作经验',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_id` (`provider_id`),
  CONSTRAINT `fk_certification_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务者认证材料表';

-- 插入初始数据
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', '13800138000', 'admin@housekeeping.com');

INSERT INTO `service_category` (`name`, `description`, `sort_order`) VALUES 
('家庭保洁', '专业的家庭清洁服务', 1),
('家电维修', '各类家电维修保养服务', 2),
('月嫂服务', '专业的月嫂护理服务', 3),
('育儿嫂', '专业的育儿护理服务', 4),
('老人护理', '专业的老人照护服务', 5),
('家庭烹饪', '专业的家庭烹饪服务', 6);

INSERT INTO `system_notice` (`title`, `content`, `type`) VALUES 
('欢迎使用家政服务管理系统', '<p>欢迎使用我们的家政服务管理系统，为您提供优质的家政服务。</p>', 1),
('系统维护通知', '<p>系统将于每周日凌晨2:00-4:00进行维护，期间可能影响正常使用。</p>', 1);

SET FOREIGN_KEY_CHECKS = 1;