package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private UserVO commenter;
    private String content;
    private Integer status;
    private LocalDateTime createdTime;
}
