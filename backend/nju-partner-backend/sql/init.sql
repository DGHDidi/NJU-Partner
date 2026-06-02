-- NJU Partner 数据库初始化脚本
-- MySQL 8.x

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS nju_partner
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nju_partner;

DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `application`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `category`;
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
    status INT DEFAULT 1 COMMENT '状态：1 正常，0 封禁',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `post` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布者 ID',
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
    status INT DEFAULT 0 COMMENT '状态：0 招募中，1 已成团，2 已关闭，3 已过期',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_post_user_id (user_id),
    INDEX idx_post_status (status),
    INDEX idx_post_campus_type (campus, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组队帖子表';

CREATE TABLE `application` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子 ID',
    user_id BIGINT NOT NULL COMMENT '报名用户 ID',
    message VARCHAR(255) COMMENT '报名留言',
    status INT DEFAULT 0 COMMENT '状态：0 待审核，1 已通过，2 已拒绝，3 已取消',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_application_post_user (post_id, user_id),
    INDEX idx_application_user_id (user_id),
    INDEX idx_application_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名表';

CREATE TABLE `comment` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子 ID',
    user_id BIGINT NOT NULL COMMENT '评论用户 ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    status INT DEFAULT 1 COMMENT '状态：1 正常，0 删除',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_comment_post_id (post_id),
    INDEX idx_comment_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

CREATE TABLE `favorite` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    post_id BIGINT NOT NULL COMMENT '帖子 ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_favorite_user_post (user_id, post_id),
    INDEX idx_favorite_post_id (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

CREATE TABLE `category` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1 启用，0 禁用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

INSERT INTO `category` (name, sort, status) VALUES
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

INSERT INTO `user` (username, password, nickname, campus, grade, major, role, status) VALUES
('admin', 'b896a419be57d0cefccb79b5079a9e74712e5078e2fa26f620e317ca1a601607', '管理员', '仙林校区', '其他', '系统管理', 'ADMIN', 1);

SET FOREIGN_KEY_CHECKS = 1;
