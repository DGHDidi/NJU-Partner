package com.nju.partner.controller;

import com.nju.partner.common.Result;
import com.nju.partner.dto.CommentCreateDTO;
import com.nju.partner.service.CommentService;
import com.nju.partner.vo.CommentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{id}/comments")
    public Result<Void> create(@PathVariable Long id, @Valid @RequestBody CommentCreateDTO dto) {
        commentService.createComment(id, dto.getContent());
        return Result.success();
    }

    @GetMapping("/posts/{id}/comments")
    public Result<List<CommentVO>> getByPost(@PathVariable Long id) {
        return Result.success(commentService.getComments(id));
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
