package com.nju.partner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Comment;
import com.nju.partner.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    void createComment(Long postId, String content);

    List<CommentVO> getPostComments(Long postId);

    void deleteComment(Long commentId);

    IPage<CommentVO> queryComments(Integer pageNum, Integer pageSize, String keyword);
}
