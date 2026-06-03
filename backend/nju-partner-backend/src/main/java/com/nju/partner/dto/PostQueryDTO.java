package com.nju.partner.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostQueryDTO {

    private String campus;
    private String type;
    private String keyword;
    private Integer status;
    private String grade;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
