package com.nju.partner.controller;

import com.nju.partner.common.Result;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @GetMapping("/profile")
    public Result<UserVO> getCurrentUserProfile() {
        return Result.success(userService.getCurrentUserProfile());
    }
}

