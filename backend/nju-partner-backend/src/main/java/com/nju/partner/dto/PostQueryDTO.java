package com.nju.partner.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

@Data
public class PostQueryDTO {

    private String campus;
    private String type;
    private String keyword;
    private Integer status;
    /**
     * 年级筛选（匹配帖子中的 grade_limit 字段）
     */
    private String grade;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
