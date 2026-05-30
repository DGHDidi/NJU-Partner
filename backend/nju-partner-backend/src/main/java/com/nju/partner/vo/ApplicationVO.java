package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationVO {

    private Long id;
    private Long postId;
    private Long userId;
    private UserVO applicant;
    private String message;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
