package com.nju.partner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在3-50位之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在6-100位之间")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @NotBlank(message = "校区不能为空")
    @Size(max = 50, message = "校区长度不能超过50")
    private String campus;

    @NotBlank(message = "年级不能为空")
    @Size(max = 50, message = "年级长度不能超过50")
    private String grade;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业长度不能超过100")
    private String major;
}

