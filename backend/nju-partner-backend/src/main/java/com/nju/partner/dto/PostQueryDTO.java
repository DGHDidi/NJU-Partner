package com.nju.partner.dto;

import lombok.Data;

@Data
public class PostQueryDTO {

    private String campus;
    private String type;
    private String keyword;
    private Integer status;
    private String grade;
    private Long userId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
