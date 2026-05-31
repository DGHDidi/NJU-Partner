package com.nju.partner.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.Result;
import com.nju.partner.common.ResultCode;
import com.nju.partner.dto.PostQueryDTO;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.service.CommentService;
import com.nju.partner.service.PostService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.CommentVO;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public AdminController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/posts")
    public Result<IPage<PostVO>> getPosts(PostQueryDTO query) {
        assertAdmin();
        return Result.success(postService.queryPosts(query));
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        assertAdmin();
        postService.removeById(id);
        return Result.success();
    }

    @GetMapping("/users")
    public Result<IPage<UserVO>> getUsers(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) String keyword) {
        assertAdmin();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedTime);
        if (StringUtils.hasText(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(User::getUsername, k).or().like(User::getNickname, k));
        }

        Page<User> page = userService.page(new Page<>(safePageNum(pageNum), safePageSize(pageSize)), wrapper);
        return Result.success(page.convert(this::toUserVO));
    }

    @PutMapping("/users/{id}/ban")
    public Result<Void> banUser(@PathVariable Long id) {
        assertAdmin();
        updateUserStatus(id, 0);
        return Result.success();
    }

    @PutMapping("/users/{id}/unban")
    public Result<Void> unbanUser(@PathVariable Long id) {
        assertAdmin();
        updateUserStatus(id, 1);
        return Result.success();
    }

    @GetMapping("/comments")
    public Result<IPage<CommentVO>> getComments(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) String keyword) {
        assertAdmin();
        return Result.success(commentService.queryComments(pageNum, pageSize, keyword));
    }

    private void updateUserStatus(Long userId, int status) {
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "不能操作管理员账号");
        }
        user.setStatus(status);
        userService.updateById(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setCampus(user.getCampus());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        return vo;
    }

    private void assertAdmin() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = userService.getById(userId);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "仅管理员可访问");
        }
    }

    private Integer safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private Integer safePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
    }
}
