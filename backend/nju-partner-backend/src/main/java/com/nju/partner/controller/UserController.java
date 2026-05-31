package com.nju.partner.controller;

import com.nju.partner.common.Result;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.UserVO;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.ApplicationVO;
import com.nju.partner.service.PostService;
import com.nju.partner.service.ApplicationService;
import com.nju.partner.service.FavoriteService;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final ApplicationService applicationService;
    private final FavoriteService favoriteService;

    @Autowired
    public UserController(UserService userService, PostService postService, ApplicationService applicationService, FavoriteService favoriteService) {
        this.userService = userService;
        this.postService = postService;
        this.applicationService = applicationService;
        this.favoriteService = favoriteService;
    }

    // Backward-compatible constructor used by some tests/clients
    public UserController(UserService userService) {
        this(userService, null, null, null);
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

    @GetMapping("/my-posts")
    public Result<java.util.List<PostVO>> myPosts() {
        return Result.success(postService.getMyPosts());
    }

    @GetMapping("/my-applications")
    public Result<java.util.List<ApplicationVO>> myApplications() {
        return Result.success(applicationService.getMyApplications());
    }

    @GetMapping("/my-favorites")
    public Result<java.util.List<PostVO>> myFavorites() {
        return Result.success(favoriteService.getMyFavorites());
    }
}

