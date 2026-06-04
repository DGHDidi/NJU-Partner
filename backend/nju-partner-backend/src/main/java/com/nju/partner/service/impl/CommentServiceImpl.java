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
import com.nju.partner.service.NotificationService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.CommentVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final PostMapper postMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    public CommentServiceImpl(PostMapper postMapper, UserService userService, NotificationService notificationService) {
        this.postMapper = postMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createComment(Long postId, String content, Long parentId) {
        Long userId = currentUserId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }

        Comment parent = null;
        if (parentId != null) {
            parent = this.getById(parentId);
            if (parent == null || parent.getStatus() == 0 || !parent.getPostId().equals(postId)) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "回复的评论不存在");
            }
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parent == null ? null : parent.getId());
        comment.setReplyToUserId(parent == null ? null : parent.getUserId());
        comment.setContent(content.trim());
        comment.setStatus(1);
        this.save(comment);

        if (parent != null && !parent.getUserId().equals(userId)) {
            User user = userService.getById(userId);
            String nickname = user == null ? "有人" : displayName(user);
            notificationService.createNotification(
                    parent.getUserId(),
                    "COMMENT_REPLIED",
                    "评论收到回复",
                    nickname + " 回复了你在《" + post.getTitle() + "》下的评论",
                    postId,
                    comment.getId(),
                    null);
        }
    }

    @Override
    public List<CommentVO> getPostComments(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }

        List<Comment> comments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreatedTime));
        return toCommentVOList(comments);
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
        Page<CommentVO> result = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        result.setRecords(toCommentVOList(commentPage.getRecords()));
        return result;
    }

    private List<CommentVO> toCommentVOList(List<Comment> comments) {
        List<Long> userIds = comments.stream()
                .flatMap(item -> java.util.stream.Stream.of(item.getUserId(), item.getReplyToUserId()))
                .filter(id -> id != null)
                .distinct()
                .toList();
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap() :
                userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, item -> item));
        List<Long> postIds = comments.stream().map(Comment::getPostId).distinct().toList();
        Map<Long, Post> postMap = postIds.isEmpty() ? Collections.emptyMap() :
                postMapper.selectBatchIds(postIds).stream().collect(Collectors.toMap(Post::getId, item -> item));
        return comments.stream().map(item -> toCommentVO(item, userMap, postMap)).toList();
    }

    private CommentVO toCommentVO(Comment comment, Map<Long, User> userMap, Map<Long, Post> postMap) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setParentId(comment.getParentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setContent(comment.getContent());
        vo.setStatus(comment.getStatus());
        vo.setCreatedTime(comment.getCreatedTime());

        User user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setUser(toUserVO(user));
        }

        User replyToUser = comment.getReplyToUserId() == null ? null : userMap.get(comment.getReplyToUserId());
        if (replyToUser != null) {
            vo.setReplyToUser(toUserVO(replyToUser));
        }

        Post post = postMap.get(comment.getPostId());
        if (post != null) {
            vo.setPostTitle(post.getTitle());
        }
        return vo;
    }

    private UserVO toUserVO(User user) {
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
        return userVO;
    }

    private String displayName(User user) {
        return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
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
