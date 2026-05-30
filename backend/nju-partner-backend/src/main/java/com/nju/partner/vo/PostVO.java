package com.nju.partner.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVO {

    private Long id;
    private Long userId;
    private UserVO publisher;
    private String title;
    private String type;
    private String description;
    private String location;
    private LocalDateTime activityTime;
    private Integer needCount;
    private Integer currentCount;
    private String campus;
    private String gradeLimit;
    private String majorLimit;
    private String contact;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}