-- 南大轻搭子 NJU Partner 数据库初始化脚本
-- MySQL 8.x

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS nju_partner
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nju_partner;

-- ----------------------------
-- user 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    campus VARCHAR(50) COMMENT '校区',
    grade VARCHAR(50) COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status INT DEFAULT 1 COMMENT '状态：1正常，0封禁',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- post 组队帖表
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    type VARCHAR(50) NOT NULL COMMENT '活动类型',
    description TEXT COMMENT '活动描述',
    location VARCHAR(100) COMMENT '具体地点',
    activity_time DATETIME COMMENT '活动时间',
    need_count INT NOT NULL COMMENT '需要人数',
    current_count INT DEFAULT 0 COMMENT '当前已通过人数',
    campus VARCHAR(50) COMMENT '活动校区',
    grade_limit VARCHAR(50) COMMENT '年级限制',
    major_limit VARCHAR(100) COMMENT '专业限制',
    contact VARCHAR(100) COMMENT '联系方式',
    status INT DEFAULT 0 COMMENT '状态：0招募中，1已成团，2已关闭，3已过期',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组队帖表';

-- ----------------------------
-- application 报名表
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '报名用户ID',
    message VARCHAR(255) COMMENT '报名留言',
    status INT DEFAULT 0 COMMENT '状态：0待审核，1已通过，2已拒绝，3已取消',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_post_user (post_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';

-- ----------------------------
-- comment 评论表
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    status INT DEFAULT 1 COMMENT '状态：1正常，0删除',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ----------------------------
-- favorite 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_post (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ----------------------------
-- category 分类表
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ----------------------------
-- 分类初始数据
-- ----------------------------
INSERT INTO category (name, sort, status) VALUES
('自习搭子', 1, 1),
('运动搭子', 2, 1),
('竞赛组队', 3, 1),
('课程学习', 4, 1),
('拼单', 5, 1),
('饭搭子', 6, 1),
('短途出行', 7, 1),
('讲座同行', 8, 1),
('社团活动', 9, 1),
('跨校区同行', 10, 1),
('其他', 99, 1);

SET FOREIGN_KEY_CHECKS = 1;
