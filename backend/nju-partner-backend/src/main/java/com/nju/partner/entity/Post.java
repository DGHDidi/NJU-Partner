package com.nju.partner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    private String title;
    private String type;
    private String description;
    private String location;

    @TableField("activity_time")
    private LocalDateTime activityTime;

    @TableField("need_count")
    private Integer needCount;

    @TableField("current_count")
    private Integer currentCount;

    private String campus;

    @TableField("grade_limit")
    private String gradeLimit;

    @TableField("major_limit")
    private String majorLimit;

    private String contact;
    private Integer status;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;
}