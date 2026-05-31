package com.nju.partner.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

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

    /**
     * 活动开始时间范围（>= startTime）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 活动结束时间范围（<= endTime）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
