package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Comment;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.CommentMapper;
import com.nju.partner.mapper.PostMapper;
import com.nju.partner.service.CommentService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.CommentVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostMapper postMapper;
    private final UserService userService;

    public CommentServiceImpl(PostMapper postMapper, UserService userService) {
        this.postMapper = postMapper;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(Long postId, String content) {
        Long userId = currentUserId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
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
    public List<CommentVO> getPostComments(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }

        return this.list(new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getStatus, 1)
                        .orderByDesc(Comment::getCreatedTime))
                .stream()
                .map(this::toCommentVO)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        Long userId = currentUserId();
        Comment comment = this.getById(commentId);
        if (comment == null || comment.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "评论不存在");
        }

        User currentUser = userService.getById(userId);
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权删除该评论");
        }

        comment.setStatus(0);
        this.updateById(comment);
    }

    @Override
    public IPage<CommentVO> queryComments(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreatedTime);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Comment::getContent, keyword.trim());
        }

        Page<Comment> commentPage = this.page(new Page<>(safePageNum(pageNum), safePageSize(pageSize)), wrapper);
        return commentPage.convert(this::toCommentVO);
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
            userVO.setStatus(user.getStatus());
            vo.setUser(userVO);
        }

        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            vo.setPostTitle(post.getTitle());
        }
        return vo;
    }

    private Long currentUserId() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    private Integer safePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private Integer safePageSize(Integer pageSize) {
        return pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
    }
}
