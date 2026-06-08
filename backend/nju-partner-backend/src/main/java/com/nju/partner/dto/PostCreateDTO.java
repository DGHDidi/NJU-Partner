package com.nju.partner.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @NotBlank(message = "活动类型不能为空")
    @Size(max = 50, message = "类型长度不能超过50")
    private String type;

    @Size(max = 500, message = "描述长度不能超过500")
    private String description;

    @Size(max = 100, message = "地点长度不能超过100")
    private String location;

    @NotNull(message = "活动时间不能为空")
    private LocalDateTime activityTime;

    @NotNull(message = "需要人数不能为空")
    @Min(value = 1, message = "需要人数至少为1")
    private Integer needCount;

    @NotBlank(message = "活动校区不能为空")
    @Size(max = 50, message = "校区长度不能超过50")
    private String campus;

    @Size(max = 50, message = "年级限制长度不能超过50")
    private String gradeLimit;

    @Size(max = 100, message = "专业限制长度不能超过100")
    private String majorLimit;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式长度不能超过100")
    private String contact;
}
