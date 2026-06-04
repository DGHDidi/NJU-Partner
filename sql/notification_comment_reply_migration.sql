USE nju_partner;

ALTER TABLE `comment`
    ADD COLUMN parent_id BIGINT NULL COMMENT '父评论ID',
    ADD COLUMN reply_to_user_id BIGINT NULL COMMENT '被回复用户ID',
    ADD INDEX idx_comment_parent_id (parent_id);

CREATE TABLE IF NOT EXISTS `notification` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(50) NOT NULL COMMENT '通知类型',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) COMMENT '通知内容',
    post_id BIGINT COMMENT '相关帖子ID',
    comment_id BIGINT COMMENT '相关评论ID',
    application_id BIGINT COMMENT '相关报名ID',
    is_read INT DEFAULT 0 COMMENT '0未读，1已读',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_notification_user_read (user_id, is_read),
    INDEX idx_notification_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
