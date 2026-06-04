package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {

    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long postId;
    private Long commentId;
    private Long applicationId;
    private Integer isRead;
    private LocalDateTime createdTime;
}
