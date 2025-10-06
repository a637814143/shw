/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : housekeeping_db

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 05/10/2025 14:59:58
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
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$DuIrhasoWV7c6xohb5ncDeyFCH/M/fjAqzkg9CAwdOKr9jXj56m7S', '系统管理员', '13800000003', 'admin@housekeeping.com', NULL, 1, '2025-09-28 19:41:08', '2025-10-03 17:52:11');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接地址',
  `service_id` bigint NULL DEFAULT NULL COMMENT '关联服务ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  CONSTRAINT `fk_banner_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
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
  `update_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_service`(`user_id` ASC, `service_id` ASC) USING BTREE,
  UNIQUE INDEX `UKg04fd00qnko405ypxjb926x4a`(`user_id` ASC, `service_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  CONSTRAINT `fk_favorite_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for housekeeping_service
-- ----------------------------
DROP TABLE IF EXISTS `housekeeping_service`;
CREATE TABLE `housekeeping_service`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服务描述',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服务详情（富文本）',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `price` decimal(10, 2) NOT NULL COMMENT '服务价格',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '次' COMMENT '服务单位',
  `duration` int NULL DEFAULT NULL COMMENT '服务时长（分钟）',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '服务图片（JSON数组）',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务标签',
  `rating` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '评分',
  `review_count` int NULL DEFAULT 0 COMMENT '评价数量',
  `booking_count` int NULL DEFAULT 0 COMMENT '预约数量',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_service_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_service_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_service_status`(`status` ASC) USING BTREE,
  INDEX `idx_service_rating`(`rating` ASC) USING BTREE,
  INDEX `idx_service_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_service_category` FOREIGN KEY (`category_id`) REFERENCES `service_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_service_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家政服务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of housekeeping_service
-- ----------------------------

-- ----------------------------
-- Table structure for housekeeping_tip
-- ----------------------------
DROP TABLE IF EXISTS `housekeeping_tip`;
CREATE TABLE `housekeeping_tip`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '贴士ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容（富文本）',
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摘要',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞次数',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '居家贴士表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of housekeeping_tip
-- ----------------------------

-- ----------------------------
-- Table structure for provider_certification
-- ----------------------------
DROP TABLE IF EXISTS `provider_certification`;
CREATE TABLE `provider_certification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证正面',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证反面',
  `health_certificate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '健康证',
  `skill_certificate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技能证书',
  `work_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工作经验',
  `status` tinyint NULL DEFAULT 0,
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_provider_id`(`provider_id` ASC) USING BTREE,
  CONSTRAINT `fk_certification_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务者认证材料表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of provider_certification
-- ----------------------------

-- ----------------------------
-- Table structure for recharge_record
-- ----------------------------
DROP TABLE IF EXISTS `recharge_record`;
CREATE TABLE `recharge_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '充值ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '充值金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式',
  `transaction_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易单号',
  `status` tinyint NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_recharge_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recharge_record
-- ----------------------------

-- ----------------------------
-- Table structure for service_booking
-- ----------------------------
DROP TABLE IF EXISTS `service_booking`;
CREATE TABLE `service_booking`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `booking_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '预约单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `service_id` bigint NOT NULL COMMENT '服务ID',
  `provider_id` bigint NOT NULL COMMENT '服务者ID',
  `booking_date` date NOT NULL COMMENT '预约日期',
  `booking_time` time NOT NULL COMMENT '预约时间',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系电话',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `status` tinyint NULL DEFAULT 0,
  `payment_status` tinyint NULL DEFAULT 0,
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_booking_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_booking_provider_id`(`provider_id` ASC) USING BTREE,
  INDEX `idx_booking_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_booking_status`(`status` ASC) USING BTREE,
  INDEX `idx_booking_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_booking_date`(`booking_date` ASC) USING BTREE,
  INDEX `idx_booking_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_booking_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_booking
-- ----------------------------

-- ----------------------------
-- Table structure for service_bookings
-- ----------------------------
DROP TABLE IF EXISTS `service_bookings`;
CREATE TABLE `service_bookings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `customer_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `notes` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `service_date` date NOT NULL,
  `status` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `service_item_id` bigint NOT NULL,
  `assigned_provider` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `created_by` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKnb930ihhrtudws9wyt4rg1ae4`(`service_item_id` ASC) USING BTREE,
  CONSTRAINT `FKnb930ihhrtudws9wyt4rg1ae4` FOREIGN KEY (`service_item_id`) REFERENCES `service_items` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_bookings
-- ----------------------------
INSERT INTO `service_bookings` VALUES (1, '8懂', '孙红伟', '61', '15825119260', '2025-10-25', 'ACCEPTED', 1, 'demo_provider', '', 0.00);

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
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_status`(`status` ASC) USING BTREE,
  INDEX `idx_category_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_category_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_category
-- ----------------------------
INSERT INTO `service_category` VALUES (1, '家庭保洁', '专业的家庭清洁服务', NULL, 1, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `service_category` VALUES (2, '家电维修', '各类家电维修保养服务', NULL, 2, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `service_category` VALUES (3, '月嫂服务', '专业的月嫂护理服务', NULL, 3, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `service_category` VALUES (4, '育儿嫂', '专业的育儿护理服务', NULL, 4, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `service_category` VALUES (5, '老人护理', '专业的老人照护服务', NULL, 5, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `service_category` VALUES (6, '家庭烹饪', '专业的家庭烹饪服务', NULL, 6, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');

-- ----------------------------
-- Table structure for service_items
-- ----------------------------
DROP TABLE IF EXISTS `service_items`;
CREATE TABLE `service_items`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_items
-- ----------------------------
INSERT INTO `service_items` VALUES (1, '2小时标准家庭保洁，包含地面、桌面和卫生间清洁。', '家庭保洁', 128);
INSERT INTO `service_items` VALUES (2, '针对厨房、卫生间的深度清洁服务，耗时约4小时。', '深度保洁', 258);
INSERT INTO `service_items` VALUES (3, '空调、油烟机等常见家电拆洗服务。', '家电清洗', 198);

-- ----------------------------
-- Table structure for service_provider
-- ----------------------------
DROP TABLE IF EXISTS `service_provider`;
CREATE TABLE `service_provider`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务者ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `work_experience` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工作经验',
  `skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '技能特长',
  `certification_status` tinyint NULL DEFAULT 0,
  `certification_time` datetime NULL DEFAULT NULL COMMENT '认证时间',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE,
  INDEX `idx_provider_username`(`username` ASC) USING BTREE,
  INDEX `idx_provider_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_provider_status`(`status` ASC) USING BTREE,
  INDEX `idx_provider_certification_status`(`certification_status` ASC) USING BTREE,
  INDEX `idx_provider_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家政服务者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_provider
-- ----------------------------
INSERT INTO `service_provider` VALUES (1, 'testprovider1759478980715', '$2a$10$IiR/7oolfscyA2tnx5TZOOBO/5OUBUPEfIhQSfLZZgLpPRZ7sPPYS', NULL, '13907954003', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0.00, 1, '2025-10-03 16:09:41', '2025-10-03 16:09:41');
INSERT INTO `service_provider` VALUES (2, 'testprovider1759480217488', '$2a$10$B1D.XwJQUR5C5NT5Z5j6Re4cFlrOMZfDgymrbggD/4Ymn3Ds3x742', NULL, '13946210622', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0.00, 1, '2025-10-03 16:30:18', '2025-10-03 16:30:18');
INSERT INTO `service_provider` VALUES (3, 'testprovider1759480347776', '$2a$10$PDcciguzD/tydt2PrbtuSur0xz3ZNavYv62NrrhxSCzwJmnJXbVxm', NULL, '13984656429', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0.00, 1, '2025-10-03 16:32:28', '2025-10-03 16:32:28');
INSERT INTO `service_provider` VALUES (4, 'testprovider', '$2a$10$i3auCOaaVZQSFlRAXDHxk.T5Fl/Gkh3dL1z7l7qWFWbYDlU5rrQPi', NULL, '13800138001', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0.00, 1, '2025-10-03 17:07:23', '2025-10-03 17:07:23');
INSERT INTO `service_provider` VALUES (5, 'provider', '$2a$10$DuIrhasoWV7c6xohb5ncDeyFCH/M/fjAqzkg9CAwdOKr9jXj56m7S', NULL, '13800000002', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 0.00, 1, '2025-10-03 17:48:05', '2025-10-03 17:52:11');
INSERT INTO `service_provider` VALUES (6, '001', '$2a$10$sjA9twgV3FZvsAqBZEtchedOvj259w54M0Oq/5YBPmAi5GUBZUoem', NULL, '13355558888', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, 0.00, 1, '2025-10-04 14:13:32', '2025-10-04 14:13:32');

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
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片（JSON数组）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_id`(`booking_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  CONSTRAINT `fk_review_booking` FOREIGN KEY (`booking_id`) REFERENCES `service_booking` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_service` FOREIGN KEY (`service_id`) REFERENCES `housekeeping_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_review
-- ----------------------------

-- ----------------------------
-- Table structure for system_notice
-- ----------------------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容（富文本）',
  `type` tinyint NULL DEFAULT 1,
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_notice
-- ----------------------------
INSERT INTO `system_notice` VALUES (1, '欢迎使用家政服务管理系统', '<p>欢迎使用我们的家政服务管理系统，为您提供优质的家政服务。</p>', 1, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');
INSERT INTO `system_notice` VALUES (2, '系统维护通知', '<p>系统将于每周日凌晨2:00-4:00进行维护，期间可能影响正常使用。</p>', 1, 1, '2025-09-28 19:41:08', '2025-09-28 19:41:08');

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
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `balance` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '账户余额',
  `status` tinyint NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_username`(`username` ASC) USING BTREE,
  INDEX `idx_user_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_status`(`status` ASC) USING BTREE,
  INDEX `idx_user_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '普通用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'Takeuchi Aoi', 'P9F1dRfhng', 'Takeuchi Aoi', '70-0563-8166', 'aot7@gmail.com', 'TiBsuNmkLd', 110, '2004-11-24', '5-2-14 Kikusui 3 Jo, Shiroishi Ward', 587.63, 102, '2007-07-06 09:08:53', '2004-01-16 01:12:46');
INSERT INTO `user` VALUES (3, 'Peng Jiehong', 'JGT7tCBwmy', 'Peng Jiehong', '28-529-6354', 'jpen86@outlook.com', 'kacICXCv8S', 73, '2015-04-12', 'No. 989, Shuangqing Rd, Chenghua District', 463.82, 53, '2020-07-17 01:14:09', '2010-08-30 17:03:47');
INSERT INTO `user` VALUES (4, 'Hara Kaito', 'c7NzgW9Tjj', 'Hara Kaito', '(116) 198 4660', 'kaihara517@gmail.com', 'UxXUK10lJA', 101, '2017-06-05', '736 Hinckley Rd', 510.90, 1, '2012-06-02 02:51:34', '2016-12-03 22:37:48');
INSERT INTO `user` VALUES (5, 'testuser1759478391825', '$2a$10$OJo4eN00YB6dPxqAk0t.e.0h9rI/eWkKS.UqQD9GuvDQt1rDlMpxi', NULL, '13825592566', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-03 15:59:52', '2025-10-03 15:59:52');
INSERT INTO `user` VALUES (6, 'testuser1759478545812', '$2a$10$fPxi3hXt.DcTnwbJrd5DH.W4d2ZmKv7rPR2DYLlK3N0vu8SHlsSk.', '测试用户', '13846253379', 'test@example.com', NULL, 1, NULL, '北京市朝阳区测试地址', 0.00, 1, '2025-10-03 16:02:26', '2025-10-03 16:14:05');
INSERT INTO `user` VALUES (7, 'testuser1759480111980', '$2a$10$eqlFx4UnrMA29nMc4yZiUOTFvboO.rya/7QkTORoFB9Ww7SHNKABa', NULL, '13840377403', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-03 16:28:32', '2025-10-03 16:28:32');
INSERT INTO `user` VALUES (8, 'testuser1759480122030', '$2a$10$lYv4YQfw4.66ufcjvJzf/eCIsAMNtR9xAI6U/xH..4BWnefB0GK.S', NULL, '13842413048', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-03 16:28:42', '2025-10-03 16:28:42');
INSERT INTO `user` VALUES (9, 'testuser', '$2a$10$DuIrhasoWV7c6xohb5ncDeyFCH/M/fjAqzkg9CAwdOKr9jXj56m7S', NULL, '13800138000', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-03 16:50:34', '2025-10-03 16:50:34');
INSERT INTO `user` VALUES (10, 'user', '$2a$10$DuIrhasoWV7c6xohb5ncDeyFCH/M/fjAqzkg9CAwdOKr9jXj56m7S', NULL, '13800000001', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-03 17:48:05', '2025-10-03 17:52:11');
INSERT INTO `user` VALUES (11, '001', '$2a$10$mVR7N89hWrmFbGINAmr6U.UIGTxqi1BFvIzQxJj/BuJM/YyZbbD.W', NULL, '13355558888', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-04 14:09:27', '2025-10-04 14:09:27');
INSERT INTO `user` VALUES (12, '123456', '$2a$10$xNYLBrRc6FW748XpXhMtPuUWFsb8aAQiRbv/ugOaa19wichmvc8Qi', NULL, '13311112222', NULL, NULL, NULL, NULL, NULL, 0.00, 1, '2025-10-04 14:16:34', '2025-10-04 14:16:34');

-- ----------------------------
-- Table structure for user_family
-- ----------------------------
DROP TABLE IF EXISTS `user_family`;
CREATE TABLE `user_family`  (
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（Base64后SHA-512）',
  `types` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色类型：ADMIN/USER/PROVIDER',
  `money` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '钱包余额',
  PRIMARY KEY (`user_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统角色账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_family
-- ----------------------------
INSERT INTO `user_family` VALUES ('admin', 'deeee38b935da476975482a333f214137c2da41ffc2fb861e6d5ce1245c58b24b5d657d65a6f0c5a6d41da1d15462e9030b44f64d41f7c8a186675984106c386', 'ADMIN', 0.00);
INSERT INTO `user_family` VALUES ('demo_provider', 'e50850268e8dbc4d650e55589346e4388a4f24e74fb6b1fb4252e3a3f1f23c493a25817324ef2a6ba4a6b9db69485ea9b6e00a9a893a940fcd5736f1bd8282d4', 'PROVIDER', 0.00);
INSERT INTO `user_family` VALUES ('demo_user', '65f8d7f9befd378cf1bea6ce91ffdf2605b74a7e143d341f5a4f6e3ace33925534b9f746500455cba10e6511e1631d491ed46916e6dcdcecc38ede0b7326b52f', 'USER', 1000.00);
INSERT INTO `user_family` VALUES ('shw', 'b95ffb5eaa7fb5b81a45dc7101ea62324287d147aa92d035bd4da12a08a1ce2fc0c1e2e4bde030e8e0f2f64c59a629986ec1d1aa218114d478f4ecc2bc01dcfe', 'USER', 1000.00);
INSERT INTO `user_family` VALUES ('user1', '7238b616a7e94ecbd6ed5f2c0db84a8725f278ae9361372f0c2bc996dd161ba88a20115cf5ec2f9525ede13dae5cf740c8162468485615e3c738f035e1f0759b', 'USER', 1000.00);
INSERT INTO `user_family` VALUES ('user2', 'd45090d6fb8a38b495a0cdaea22a27baa55985963f72468c8c6e29ed7d70230fb5fcf94f75edcb2dd581b260f3dea66f528b0b25a265bd4c9593597b7d87cc54', 'PROVIDER', 0.00);

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
  `status` tinyint NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_provider_id`(`provider_id` ASC) USING BTREE,
  CONSTRAINT `fk_withdraw_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提现记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of withdraw_record
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
