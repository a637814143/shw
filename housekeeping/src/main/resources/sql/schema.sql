-- 家政服务管理系统数据库表结构

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 统一登录账号表
CREATE TABLE IF NOT EXISTS `auth_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `account` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(255) NOT NULL COMMENT '加密密码',
  `role` varchar(20) NOT NULL COMMENT '角色：admin/staff/user',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一登录账号表';

-- 普通用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '余额',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='普通用户表';

-- 家政服务者表
CREATE TABLE IF NOT EXISTS `provider` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务者ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `id_card_front` varchar(255) DEFAULT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) DEFAULT NULL COMMENT '身份证反面照',
  `certificate` varchar(255) DEFAULT NULL COMMENT '职业证书',
  `experience` text COMMENT '工作经验',
  `balance` decimal(10,2) DEFAULT 0.00 COMMENT '余额',
  `rating` decimal(3,2) DEFAULT 0.00 COMMENT '评分',
  `service_count` int DEFAULT 0 COMMENT '服务次数',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-已禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务者表';

-- 服务分类表
CREATE TABLE IF NOT EXISTS `service_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` text COMMENT '分类描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务分类表';

-- 家政服务表
CREATE TABLE IF NOT EXISTS `housekeeping_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `title` varchar(200) NOT NULL COMMENT '服务标题',
  `description` text COMMENT '服务描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `unit` varchar(20) DEFAULT '次' COMMENT '单位',
  `images` text COMMENT '服务图片，JSON格式',
  `service_area` varchar(200) DEFAULT NULL COMMENT '服务区域',
  `service_time` varchar(100) DEFAULT NULL COMMENT '服务时间',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_service_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`),
  CONSTRAINT `fk_service_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务表';

-- 服务预约表
CREATE TABLE IF NOT EXISTS `service_booking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint DEFAULT NULL COMMENT '分配的服务者ID',
  `booking_no` varchar(50) NOT NULL COMMENT '预约单号',
  `service_time` datetime NOT NULL COMMENT '预约服务时间',
  `address` varchar(500) NOT NULL COMMENT '服务地址',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `remark` text COMMENT '备注',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待分配，1-已分配，2-服务中，3-已完成，4-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_no` (`booking_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_booking_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`),
  CONSTRAINT `fk_booking_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务预约表';

-- 充值记录表
CREATE TABLE IF NOT EXISTS `recharge_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '充值ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(100) DEFAULT NULL COMMENT '交易单号',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_recharge_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- 提现记录表
CREATE TABLE IF NOT EXISTS `withdraw_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提现ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `amount` decimal(10,2) NOT NULL COMMENT '提现金额',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `account_holder` varchar(50) DEFAULT NULL COMMENT '开户人',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_withdraw_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- 轮播图表
CREATE TABLE IF NOT EXISTS `banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) DEFAULT NULL COMMENT '链接URL',
  `service_id` bigint DEFAULT NULL COMMENT '关联的服务ID',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  CONSTRAINT `fk_banner_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 居家贴士表
CREATE TABLE IF NOT EXISTS `housekeeping_tip` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '贴士ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='居家贴士表';

-- 服务评价表
CREATE TABLE IF NOT EXISTS `service_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `booking_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片，JSON格式',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_booking_id` (`booking_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_provider_id` (`provider_id`),
  CONSTRAINT `fk_review_booking` FOREIGN KEY (`booking_id`) REFERENCES `service_booking` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`),
  CONSTRAINT `fk_review_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
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

-- 系统公告表
CREATE TABLE IF NOT EXISTS `system_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `type` tinyint DEFAULT 1 COMMENT '类型：1-系统公告，2-活动公告',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- 用户仪表盘服务项目
CREATE TABLE IF NOT EXISTS `dashboard_service_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '服务名称',
  `description` varchar(255) NOT NULL COMMENT '服务描述',
  `icon` varchar(20) NOT NULL COMMENT '图标',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前台仪表盘服务条目表';

-- 用户仪表盘居家贴士
CREATE TABLE IF NOT EXISTS `dashboard_tip_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(150) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前台仪表盘贴士表';

-- 用户仪表盘服务评价
CREATE TABLE IF NOT EXISTS `dashboard_review_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `author` varchar(50) NOT NULL COMMENT '评价人',
  `service_name` varchar(100) NOT NULL COMMENT '服务名称',
  `rating` int NOT NULL COMMENT '评分',
  `content` text NOT NULL COMMENT '评价内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前台仪表盘评价表';

-- 用户仪表盘特惠
CREATE TABLE IF NOT EXISTS `dashboard_offer_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(150) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `tag` varchar(50) DEFAULT NULL COMMENT '标签',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='前台仪表盘特惠表';

-- 插入初始管理员数据
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '系统管理员', '13800138000', 'admin@housekeeping.com', 1);

-- 插入初始服务分类数据
INSERT INTO `service_category` (`name`, `description`, `icon`, `sort_order`, `status`) VALUES
('清洁服务', '专业家庭清洁服务', 'clean', 1, 1),
('维修服务', '家电维修、管道维修等', 'repair', 2, 1),
('护理服务', '老人护理、婴儿护理等', 'care', 3, 1),
('搬家服务', '专业搬家、搬运服务', 'move', 4, 1),
('其他服务', '其他家政相关服务', 'other', 5, 1);
