-- 家政服务平台数据库结构与初始化数据

DROP TABLE IF EXISTS `user_all`;
CREATE TABLE `user_all` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL UNIQUE COMMENT '登录账号',
  `passwd` varchar(255) NOT NULL COMMENT '两次Base64再ROT13后的密码',
  `money` decimal(12,2) NOT NULL DEFAULT 1000.00 COMMENT '账户余额（CNY）',
  `usertype` varchar(32) NOT NULL COMMENT '用户类型：普通用户/家政公司/系统管理员',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一用户账号表';

DROP TABLE IF EXISTS `housekeep`;
CREATE TABLE `housekeep` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_type` varchar(20) NOT NULL COMMENT '数据类型：service/tip/review/offer',
  `title` varchar(200) DEFAULT NULL COMMENT '标题或名称',
  `content` text COMMENT '正文内容',
  `icon` varchar(32) DEFAULT NULL COMMENT '图标/标识',
  `tag` varchar(50) DEFAULT NULL COMMENT '标签',
  `author` varchar(100) DEFAULT NULL COMMENT '作者或评价人',
  `service_name` varchar(200) DEFAULT NULL COMMENT '关联服务名称',
  `rating` decimal(3,1) DEFAULT NULL COMMENT '评分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政内容信息表';

DROP TABLE IF EXISTS `housekeep_service`;
CREATE TABLE `housekeep_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_id` bigint NOT NULL COMMENT '所属家政公司',
  `name` varchar(200) NOT NULL COMMENT '服务名称',
  `unit` varchar(50) NOT NULL COMMENT '计价单位',
  `price` decimal(12,2) NOT NULL COMMENT '服务价格',
  `contact` varchar(100) NOT NULL COMMENT '联系方式',
  `description` varchar(500) DEFAULT NULL COMMENT '服务描述',
  PRIMARY KEY (`id`),
  KEY `idx_company` (`company_id`),
  CONSTRAINT `fk_service_company` FOREIGN KEY (`company_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政公司发布的服务';

DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `status` varchar(32) NOT NULL COMMENT '订单状态',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `refund_response` varchar(500) DEFAULT NULL COMMENT '退款处理说明',
  `handled_by` bigint DEFAULT NULL COMMENT '处理人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_service` (`service_id`),
  KEY `idx_order_user` (`user_id`),
  CONSTRAINT `fk_order_service` FOREIGN KEY (`service_id`) REFERENCES `housekeep_service` (`id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`),
  CONSTRAINT `fk_order_handler` FOREIGN KEY (`handled_by`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务订单表';

DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `user_id` bigint NOT NULL COMMENT '评价人',
  `rating` int NOT NULL COMMENT '评分',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime NOT NULL COMMENT '评价时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_service` (`service_id`),
  KEY `idx_review_user` (`user_id`),
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeep_service` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

-- 初始化系统管理员账号
INSERT INTO `user_all` (`username`, `passwd`, `money`, `usertype`)
VALUES ('admin', 'JIqFqTSKAQ0=', 1000.00, '系统管理员')
ON DUPLICATE KEY UPDATE `usertype`='系统管理员';
