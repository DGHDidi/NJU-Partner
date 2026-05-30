package com.nju.partner.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationCreateDTO {

    @Size(max = 255, message = "留言长度不能超过255")
    private String message;
}
