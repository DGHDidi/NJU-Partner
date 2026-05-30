package com.nju.partner.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nju.partner.common.Result;
import com.nju.partner.dto.PostCreateDTO;
import com.nju.partner.dto.PostQueryDTO;
import com.nju.partner.service.PostService;
import com.nju.partner.vo.PostVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody PostCreateDTO dto) {
        postService.createPost(dto);
        return Result.success();
    }

    @GetMapping
    public Result<IPage<PostVO>> query(PostQueryDTO query) {
        return Result.success(postService.queryPosts(query));
    }

    @GetMapping("/{id}")
    public Result<PostVO> detail(@PathVariable Long id) {
        return Result.success(postService.getPostDetail(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PostCreateDTO dto) {
        postService.updatePost(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/close")
    public Result<Void> close(@PathVariable Long id) {
        postService.closePost(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }
}
