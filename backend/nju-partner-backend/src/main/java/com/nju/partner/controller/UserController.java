package com.nju.partner.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nju.partner.common.Result;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.dto.UserUpdateDTO;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.ApplicationVO;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PutMapping("/profile")
    public Result<UserVO> updateCurrentUserProfile(@RequestBody UserUpdateDTO dto) {
        return Result.success(userService.updateCurrentUserProfile(dto));
    }

    @GetMapping("/my-posts")
    public Result<IPage<PostVO>> getMyPosts(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getMyPosts(pageNum, pageSize));
    }

    @GetMapping("/my-applications")
    public Result<IPage<ApplicationVO>> getMyApplications(@RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getMyApplications(pageNum, pageSize));
    }

    @GetMapping("/favorites")
    public Result<IPage<PostVO>> getMyFavorites(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userService.getMyFavorites(pageNum, pageSize));
    }
}
