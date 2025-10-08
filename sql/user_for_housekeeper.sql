CREATE DATABASE IF NOT EXISTS housekeeping CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE housekeeping;

CREATE TABLE IF NOT EXISTS user_for_housekeeper (
    username VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '用户名',
    password VARCHAR(64) NOT NULL COMMENT '密码（Base64 -> 倒置 -> MD5）',
    usertype VARCHAR(32) NOT NULL COMMENT '用户类型（admin/user/provider）',
    usermoney DECIMAL(10,2) DEFAULT 0 COMMENT '账户余额'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '家政平台用户表';

INSERT INTO user_for_housekeeper (username, password, usertype, usermoney) VALUES
    ('admin', 'f5c373fcba202c9bdaf15caf70780235', 'admin', 1000.00),
    ('provider', 'ffd5c43bf98d28dcf651f07a637abb05', 'provider', 500.00),
    ('user', '969af486f1215c68555b405f84c586cc', 'user', 200.00)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    usertype = VALUES(usertype),
    usermoney = VALUES(usermoney);
