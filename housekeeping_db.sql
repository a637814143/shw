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

-- 初始化系统管理员账号
INSERT INTO `user_all` (`username`, `passwd`, `money`, `usertype`)
VALUES ('admin', 'JIqFqTSKAQ0=', 1000.00, '系统管理员')
ON DUPLICATE KEY UPDATE `usertype`='系统管理员';
