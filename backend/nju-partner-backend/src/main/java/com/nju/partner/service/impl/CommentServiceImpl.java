package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Comment;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.CommentMapper;
import com.nju.partner.service.CommentService;
import com.nju.partner.service.PostService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.CommentVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostService postService;
    private final UserService userService;

    public CommentServiceImpl(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(Long postId, String content) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (postService.getById(postId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        comment.setStatus(1);
        this.save(comment);
    }

    @Override
    public List<CommentVO> getComments(Long postId) {
        List<Comment> comments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreatedTime));
        return comments.stream().map(this::toCommentVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "评论不存在");
        }
        User user = userService.getById(userId);
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        if (!comment.getUserId().equals(userId) && !isAdmin) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只能删除自己的评论");
        }
        comment.setStatus(0);
        this.updateById(comment);
    }

    private CommentVO toCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedTime(comment.getCreatedTime());
        User user = userService.getById(comment.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatar(user.getAvatar());
            userVO.setCampus(user.getCampus());
            userVO.setGrade(user.getGrade());
            userVO.setMajor(user.getMajor());
            userVO.setRole(user.getRole());
            vo.setCommenter(userVO);
        }
        return vo;
    }
}
