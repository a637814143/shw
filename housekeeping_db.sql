-- 家政服务平台数据库结构与初始化数据

DROP TABLE IF EXISTS `user_all`;
CREATE TABLE `user_all` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(64) NOT NULL UNIQUE COMMENT '登录账号',
  `passwd` varchar(255) NOT NULL COMMENT '两次Base64再ROT13后的密码',
  `money` decimal(12,2) NOT NULL DEFAULT 1000.00 COMMENT '账户余额（CNY）',
  `loyalty_points` int NOT NULL DEFAULT 0 COMMENT '消费累积积分',
  `usertype` varchar(32) NOT NULL COMMENT '用户类型：普通用户/家政公司/系统管理员',
  `display_name` varchar(100) NOT NULL DEFAULT '' COMMENT '展示名称',
  `avatar_base64` longtext NOT NULL COMMENT 'Base64 格式头像',
  `contact_phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `contact_address` varchar(255) DEFAULT NULL COMMENT '联系地址',
  `company_description` varchar(1000) DEFAULT NULL COMMENT '公司简介',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一用户账号表';

DROP TABLE IF EXISTS `housekeep`;
CREATE TABLE `housekeep` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_type` varchar(20) NOT NULL COMMENT '数据类型：service/tip/review/offer/carousel/announcement',
  `title` varchar(200) DEFAULT NULL COMMENT '标题或名称',
  `content` longtext COMMENT '正文内容（可存储较大 Base64 图片）',
  `icon` varchar(32) DEFAULT NULL COMMENT '图标/标识',
  `tag` varchar(50) DEFAULT NULL COMMENT '标签',
  `author` varchar(100) DEFAULT NULL COMMENT '作者或评价人',
  `service_name` varchar(200) DEFAULT NULL COMMENT '关联服务名称',
  `rating` decimal(3,1) DEFAULT NULL COMMENT '评分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政内容信息表';

DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务分类';

DROP TABLE IF EXISTS `housekeep_service`;
CREATE TABLE `housekeep_service` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_id` bigint NOT NULL COMMENT '所属家政公司',
  `category_id` bigint DEFAULT NULL COMMENT '服务分类',
  `name` varchar(200) NOT NULL COMMENT '服务名称',
  `unit` varchar(50) NOT NULL COMMENT '计价单位',
  `price` decimal(12,2) NOT NULL COMMENT '服务价格',
  `contact` varchar(100) NOT NULL COMMENT '联系方式',
  `service_time` varchar(100) NOT NULL DEFAULT '2小时' COMMENT '服务时段或耗时',
  `description` varchar(500) DEFAULT NULL COMMENT '服务描述',
  PRIMARY KEY (`id`),
  KEY `idx_company` (`company_id`),
  KEY `idx_service_category` (`category_id`),
  CONSTRAINT `fk_service_company` FOREIGN KEY (`company_id`) REFERENCES `user_all` (`id`),
  CONSTRAINT `fk_service_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政公司发布的服务';

DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `amount` decimal(12,2) NOT NULL COMMENT '实际支付金额',
  `status` varchar(32) NOT NULL COMMENT '订单状态',
  `scheduled_at` datetime NOT NULL COMMENT '预约上门时间',
  `special_request` varchar(500) DEFAULT NULL COMMENT '用户特殊需求',
  `service_address` varchar(255) DEFAULT NULL COMMENT '上门地址',
  `progress_note` varchar(500) DEFAULT NULL COMMENT '服务进度备注',
  `loyalty_points` int NOT NULL DEFAULT 0 COMMENT '本单积分',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `refund_response` varchar(500) DEFAULT NULL COMMENT '退款处理说明',
  `handled_by` bigint DEFAULT NULL COMMENT '处理人',
  `assigned_worker` varchar(100) DEFAULT NULL COMMENT '分配的家政人员',
  `worker_contact` varchar(100) DEFAULT NULL COMMENT '家政人员联系方式',
  `user_confirmed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户是否确认订单',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `settlement_released` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已结算至商家',
  `settlement_released_at` datetime DEFAULT NULL COMMENT '结算时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_service` (`service_id`),
  KEY `idx_order_user` (`user_id`),
  CONSTRAINT `fk_order_service` FOREIGN KEY (`service_id`) REFERENCES `housekeep_service` (`id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`),
  CONSTRAINT `fk_order_handler` FOREIGN KEY (`handled_by`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政服务订单表';

DROP TABLE IF EXISTS `service_favorite`;
CREATE TABLE `service_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_id` bigint NOT NULL COMMENT '收藏的服务',
  `user_id` bigint NOT NULL COMMENT '收藏用户',
  `created_at` datetime NOT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_user_service` (`user_id`, `service_id`),
  KEY `idx_favorite_service` (`service_id`),
  CONSTRAINT `fk_favorite_service` FOREIGN KEY (`service_id`) REFERENCES `housekeep_service` (`id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务收藏表';

DROP TABLE IF EXISTS `account_transaction`;
CREATE TABLE `account_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '关联账号',
  `txn_type` varchar(20) NOT NULL COMMENT '交易类型：RECHARGE/WITHDRAW/ADJUST',
  `amount` decimal(12,2) NOT NULL COMMENT '交易金额',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_txn_user` (`user_id`),
  KEY `idx_txn_type_time` (`txn_type`, `created_at`),
  CONSTRAINT `fk_txn_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户资金流水记录';

DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `user_id` bigint NOT NULL COMMENT '评价人',
  `rating` int NOT NULL COMMENT '评分',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `created_at` datetime NOT NULL COMMENT '评价时间',
  `updated_at` datetime NOT NULL COMMENT '最后更新时间',
  `company_reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
  `reply_at` datetime DEFAULT NULL COMMENT '回复时间',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否精选',
  PRIMARY KEY (`id`),
  KEY `idx_review_service` (`service_id`),
  KEY `idx_review_user` (`user_id`),
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeep_service` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价表';

DROP TABLE IF EXISTS `company_message`;
CREATE TABLE `company_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NOT NULL COMMENT '关联订单',
  `sender_id` bigint NOT NULL COMMENT '发送者',
  `recipient_id` bigint NOT NULL COMMENT '接收者',
  `content` varchar(1000) NOT NULL COMMENT '消息内容',
  `created_at` datetime NOT NULL COMMENT '发送时间',
  `read_at` datetime DEFAULT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  KEY `idx_message_order` (`order_id`),
  KEY `idx_message_recipient` (`recipient_id`),
  CONSTRAINT `fk_message_order` FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user_all` (`id`),
  CONSTRAINT `fk_message_recipient` FOREIGN KEY (`recipient_id`) REFERENCES `user_all` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业与客户沟通消息表';

DROP TABLE IF EXISTS `company_staff`;
CREATE TABLE `company_staff` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `company_id` bigint NOT NULL COMMENT '所属家政公司',
  `category_id` bigint DEFAULT NULL COMMENT '服务分类',
  `staff_name` varchar(100) NOT NULL COMMENT '人员姓名',
  `contact` varchar(100) NOT NULL COMMENT '联系方式',
  `role` varchar(100) DEFAULT NULL COMMENT '职位或技能',
  `notes` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `service_time_slots` varchar(200) DEFAULT NULL COMMENT '可服务时间段',
  `assigned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已分配',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_staff_company` (`company_id`),
  KEY `idx_staff_category` (`category_id`),
  CONSTRAINT `fk_staff_company` FOREIGN KEY (`company_id`) REFERENCES `user_all` (`id`),
  CONSTRAINT `fk_staff_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家政公司人员信息';

-- 初始化系统管理员账号
INSERT INTO `user_all` (`username`, `passwd`, `money`, `usertype`, `display_name`, `avatar_base64`)
VALUES (
  'admin',
  'JIqFqTSKAQ0=',
  1000.00,
  '系统管理员',
  '系统管理员',
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg=='
)
ON DUPLICATE KEY UPDATE
  `usertype`='系统管理员',
  `display_name`='系统管理员',
  `avatar_base64`='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGMAAQAABQABDQottAAAAABJRU5ErkJggg==';
