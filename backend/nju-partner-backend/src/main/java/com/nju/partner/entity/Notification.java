package com.nju.partner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String type;
    private String title;
    private String content;

    @TableField("post_id")
    private Long postId;

    @TableField("comment_id")
    private Long commentId;

    @TableField("application_id")
    private Long applicationId;

    @TableField("is_read")
    private Integer isRead;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
