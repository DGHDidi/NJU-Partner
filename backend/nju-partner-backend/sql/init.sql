-- ============================================
-- 南大轻搭子 NJU Partner 数据库初始化脚本
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS nju_partner
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE nju_partner;

-- ============================================
-- 1. user 用户表
-- ============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password     VARCHAR(100) NOT NULL COMMENT '密码',
    nickname     VARCHAR(50) COMMENT '昵称',
    avatar       VARCHAR(255) COMMENT '头像',
    campus       VARCHAR(50) COMMENT '校区',
    grade        VARCHAR(50) COMMENT '年级',
    major        VARCHAR(100) COMMENT '专业',
    role         VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status       INT         DEFAULT 1 COMMENT '状态：1正常，0封禁',
    created_time DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- ============================================
-- 2. post 组队帖表
-- ============================================
DROP TABLE IF EXISTS post;
CREATE TABLE post
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL COMMENT '发布者ID',
    title         VARCHAR(100) NOT NULL COMMENT '标题',
    type          VARCHAR(50)  NOT NULL COMMENT '活动类型',
    description   TEXT COMMENT '活动描述',
    location      VARCHAR(100) COMMENT '具体地点',
    activity_time DATETIME COMMENT '活动时间',
    need_count    INT          NOT NULL COMMENT '需要人数',
    current_count INT      DEFAULT 0 COMMENT '当前已通过人数',
    campus        VARCHAR(50) COMMENT '活动校区',
    grade_limit   VARCHAR(50) COMMENT '年级限制',
    major_limit   VARCHAR(100) COMMENT '专业限制',
    contact       VARCHAR(100) COMMENT '联系方式',
    status        INT      DEFAULT 0 COMMENT '状态：0招募中，1已成团，2已关闭，3已过期',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='组队帖表';