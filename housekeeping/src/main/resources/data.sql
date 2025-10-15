-- 插入初始管理员数据（密码：admin123）
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5DCi', '系统管理员', '13800138000', 'admin@housekeeping.com', 1);

-- 插入初始服务分类数据
INSERT INTO `service_category` (`name`, `description`, `icon`, `sort_order`, `status`) VALUES
('清洁服务', '专业家庭清洁服务', 'clean', 1, 1),
('维修服务', '家电维修、管道维修等', 'repair', 2, 1),
('护理服务', '老人护理、婴儿护理等', 'care', 3, 1),
('搬家服务', '专业搬家、搬运服务', 'move', 4, 1),
('其他服务', '其他家政相关服务', 'other', 5, 1);

-- 插入示例系统公告
INSERT INTO `system_notice` (`title`, `content`, `type`, `status`) VALUES
('欢迎使用家政服务管理系统', '欢迎使用我们的家政服务管理系统，为您提供优质的家政服务。', 1, 1),
('系统维护通知', '系统将于本周末进行维护升级，期间可能影响正常使用，请提前做好准备。', 1, 1),
('新功能上线', '新增服务评价功能，用户可以对服务进行评价，帮助其他用户选择优质服务。', 2, 1);
