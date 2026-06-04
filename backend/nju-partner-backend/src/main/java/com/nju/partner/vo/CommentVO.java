package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private Long replyToUserId;
    private String content;
    private Integer status;
    private LocalDateTime createdTime;
    private UserVO user;
    private UserVO replyToUser;
    private String postTitle;
}
