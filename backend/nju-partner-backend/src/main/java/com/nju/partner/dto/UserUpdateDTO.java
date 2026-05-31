package com.nju.partner.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 50, message = "校区长度不能超过50")
    private String campus;

    @Size(max = 50, message = "年级长度不能超过50")
    private String grade;

    @Size(max = 100, message = "专业长度不能超过100")
    private String major;

    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatar;
}
