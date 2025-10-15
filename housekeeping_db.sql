/*
 家政服务管理系统数据库
 基于项目功能需求重新设计
 
 主要功能模块：
 1. 用户管理（管理员、家政人员、普通用户）
 2. 服务管理（服务分类、家政服务、预约管理）
 3. 财务管理（充值、提现、交易记录）
 4. 内容管理（轮播图、居家贴士、系统公告）
 5. 评价反馈（服务评价、收藏管理）
 6. 认证管理（服务者认证审核）
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '系统管理员', '13800138000', 'admin@housekeeping.com', 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for auth_account
-- ----------------------------
DROP TABLE IF EXISTS `auth_account`;
CREATE TABLE `auth_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色：admin/staff/user',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account`(`account` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '统一登录账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of auth_account
-- ----------------------------
INSERT INTO `auth_account` VALUES
(1, 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', 'USER', '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '普通用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES 
(1, 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '张三', '13800138001', 'zhangsan@example.com', NULL, 1000.00, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, 'user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '李四', '13800138002', 'lisi@example.com', NULL, 500.00, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for provider
-- ----------------------------
DROP TABLE IF EXISTS `provider`;
CREATE TABLE `provider`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务者ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工作经验',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '余额',
  `rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '评分',
  `service_count` int NULL DEFAULT 0 COMMENT '服务次数',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家政服务者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of provider
-- ----------------------------
INSERT INTO `provider` VALUES 
(1, 'provider001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '王师傅', '13900139001', 'wangshi@example.com', NULL, '110101199001011234', '专业清洁服务，5年经验', 0.00, 4.8, 50, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, 'provider002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '李师傅', '13900139002', 'lishi@example.com', NULL, '110101199002021234', '家电维修专家，10年经验', 0.00, 4.9, 80, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for service_category
-- ----------------------------
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_category
-- ----------------------------
INSERT INTO `service_category` VALUES (1, '清洁服务', '专业家庭清洁服务', 'clean', 1, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');
INSERT INTO `service_category` VALUES (2, '维修服务', '家电维修、管道维修等', 'repair', 2, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');
INSERT INTO `service_category` VALUES (3, '护理服务', '老人护理、婴儿护理等', 'care', 3, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');
INSERT INTO `service_category` VALUES (4, '搬家服务', '专业搬家、搬运服务', 'move', 4, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');
INSERT INTO `service_category` VALUES (5, '其他服务', '其他家政相关服务', 'other', 5, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for housekeeping_service
-- ----------------------------
DROP TABLE IF EXISTS `housekeeping_service`;
CREATE TABLE `housekeeping_service`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服务描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '次' COMMENT '单位',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服务图片，JSON格式',
  `service_area` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务区域',
  `service_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务时间',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_price`(`price` ASC) USING BTREE,
  CONSTRAINT `fk_service_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_service_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家政服务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of housekeeping_service
-- ----------------------------
INSERT INTO `housekeeping_service` VALUES 
(1, 1, 1, '专业家庭清洁', '提供专业的家庭清洁服务，包括客厅、卧室、厨房、卫生间等全屋清洁', 150.00, '次', '["/images/clean1.jpg", "/images/clean2.jpg"]', '北京市朝阳区', '周一至周日 8:00-18:00', 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, 2, 2, '家电维修服务', '专业家电维修，包括空调、洗衣机、冰箱、电视等各类家电', 200.00, '次', '["/images/repair1.jpg", "/images/repair2.jpg"]', '北京市海淀区', '周一至周日 9:00-17:00', 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for service_booking
-- ----------------------------
DROP TABLE IF EXISTS `service_booking`;
CREATE TABLE `service_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NULL DEFAULT NULL COMMENT '分配的服务者ID',
  `booking_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约单号',
  `service_time` datetime NOT NULL COMMENT '预约服务时间',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务地址',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人电话',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-待确认，2-已确认，3-已完成，0-已取消',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_service_time`(`service_time` ASC) USING BTREE,
  CONSTRAINT `fk_booking_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_booking
-- ----------------------------

-- ----------------------------
-- Table structure for service_review
-- ----------------------------
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `booking_id` bigint NOT NULL COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `rating` int NOT NULL COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片，JSON格式',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_booking_id`(`booking_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_rating`(`rating` ASC) USING BTREE,
  CONSTRAINT `fk_review_booking` FOREIGN KEY (`booking_id`) REFERENCES `service_booking` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_review
-- ----------------------------

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_service`(`user_id` ASC, `service_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '充值ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '充值金额',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易单号',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-成功，0-失败',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_recharge_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recharge_record
-- ----------------------------

-- ----------------------------
-- Table structure for withdraw_record
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_record`;
CREATE TABLE `withdraw_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提现ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '提现金额',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行账号',
  `account_holder` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-待审核，2-已通过，0-已拒绝',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_withdraw_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提现记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdraw_record
-- ----------------------------

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接URL',
  `service_id` bigint NULL DEFAULT NULL COMMENT '关联的服务ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  CONSTRAINT `fk_banner_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES 
(1, '专业家政服务', '/images/banner1.jpg', '/services', 1, 1, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, '家电维修专家', '/images/banner2.jpg', '/services', 2, 2, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for housekeeping_tip
-- ----------------------------
DROP TABLE IF EXISTS `housekeeping_tip`;
CREATE TABLE `housekeeping_tip`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '贴士ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片URL',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_view_count`(`view_count` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '居家贴士表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of housekeeping_tip
-- ----------------------------
INSERT INTO `housekeeping_tip` VALUES 
(1, '如何保持厨房清洁', '厨房是家庭中最容易脏乱的地方，建议每天饭后及时清理，定期深度清洁，使用合适的清洁剂和工具。', '/images/tip1.jpg', '清洁技巧', 156, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, '家电保养小贴士', '定期清洁家电表面，注意防潮防尘，按照说明书进行保养，可延长家电使用寿命。', '/images/tip2.jpg', '家电保养', 89, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for system_notice
-- ----------------------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `type` int NOT NULL DEFAULT 1 COMMENT '类型：1-系统公告，2-活动公告',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dashboard_service_item
-- ----------------------------
DROP TABLE IF EXISTS `dashboard_service_item`;
CREATE TABLE `dashboard_service_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务描述',
  `icon` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '前台仪表盘服务条目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dashboard_tip_item
-- ----------------------------
DROP TABLE IF EXISTS `dashboard_tip_item`;
CREATE TABLE `dashboard_tip_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '前台仪表盘贴士表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dashboard_review_item
-- ----------------------------
DROP TABLE IF EXISTS `dashboard_review_item`;
CREATE TABLE `dashboard_review_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评价人',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `rating` int NOT NULL COMMENT '评分',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '前台仪表盘评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dashboard_offer_item
-- ----------------------------
DROP TABLE IF EXISTS `dashboard_offer_item`;
CREATE TABLE `dashboard_offer_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '前台仪表盘特惠表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_notice
-- ----------------------------
INSERT INTO `system_notice` VALUES 
(1, '平台服务升级通知', '为了更好地为用户提供服务，平台将于本周末进行系统升级，升级期间可能影响部分功能使用，请用户提前做好准备。', 1, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59'),
(2, '新用户优惠活动', '新注册用户可享受首次服务8折优惠，活动时间：2025年10月1日-2025年10月31日', 2, 1, '2025-10-10 19:06:59', '2025-10-10 19:06:59');

-- ----------------------------
-- Table structure for provider_verification
-- ----------------------------
DROP TABLE IF EXISTS `provider_verification`;
CREATE TABLE `provider_verification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证反面照',
  `certificate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职业证书',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-待审核，2-已通过，0-已拒绝',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '审核管理员ID',
  `admin_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理员备注',
  `verify_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  CONSTRAINT `fk_verification_provider` FOREIGN KEY (`provider_id`) REFERENCES `provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_verification_admin` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务者认证表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of provider_verification
-- ----------------------------

-- ----------------------------
-- 创建触发器
-- ----------------------------

-- 更新服务者评分触发器
DELIMITER $$
CREATE TRIGGER `update_provider_rating` AFTER INSERT ON `service_review`
FOR EACH ROW
BEGIN
    UPDATE `provider` 
    SET `rating` = (
        SELECT AVG(`rating`) 
        FROM `service_review` 
        WHERE `provider_id` = NEW.provider_id
    ),
    `service_count` = (
        SELECT COUNT(*) 
        FROM `service_booking` 
        WHERE `provider_id` = NEW.provider_id AND `status` = 3
    )
    WHERE `id` = NEW.provider_id;
END$$
DELIMITER ;

-- 自动生成预约单号触发器
DELIMITER $$
CREATE TRIGGER `generate_booking_no` BEFORE INSERT ON `service_booking`
FOR EACH ROW
BEGIN
    IF NEW.booking_no IS NULL OR NEW.booking_no = '' THEN
        SET NEW.booking_no = CONCAT('BK', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD((SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='service_booking'), 6, '0'));
    END IF;
END$$
DELIMITER ;

-- ----------------------------
-- 创建视图
-- ----------------------------

-- 服务统计视图
CREATE VIEW `service_statistics` AS
SELECT 
    hs.id,
    hs.title,
    hs.price,
    p.real_name as provider_name,
    p.rating as provider_rating,
    COUNT(sb.id) as booking_count,
    AVG(sr.rating) as avg_rating,
    COUNT(sr.id) as review_count
FROM `housekeeping_service` hs
LEFT JOIN `provider` p ON hs.provider_id = p.id
LEFT JOIN `service_booking` sb ON hs.id = sb.service_id
LEFT JOIN `service_review` sr ON hs.id = sr.service_id
WHERE hs.status = 1
GROUP BY hs.id;

-- 用户统计视图
CREATE VIEW `user_statistics` AS
SELECT 
    u.id,
    u.username,
    u.real_name,
    u.phone,
    u.balance,
    COUNT(sb.id) as booking_count,
    SUM(sb.total_amount) as total_spent,
    COUNT(sr.id) as review_count
FROM `user` u
LEFT JOIN `service_booking` sb ON u.id = sb.user_id
LEFT JOIN `service_review` sr ON u.id = sr.user_id
WHERE u.status = 1
GROUP BY u.id;

SET FOREIGN_KEY_CHECKS = 1;