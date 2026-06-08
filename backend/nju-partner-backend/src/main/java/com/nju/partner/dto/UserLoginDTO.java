package com.nju.partner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 10, message = "用户名长度需在1-10位之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}

