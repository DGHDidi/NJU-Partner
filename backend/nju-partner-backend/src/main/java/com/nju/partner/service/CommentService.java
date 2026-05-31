package com.nju.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Comment;
import com.nju.partner.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    void createComment(Long postId, String content);

    List<CommentVO> getComments(Long postId);

    void deleteComment(Long commentId);
}
