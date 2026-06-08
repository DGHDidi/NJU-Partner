package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String message;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private UserVO applicant;
    private String postTitle;
    private Integer postStatus;
}
