package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.dto.PostCreateDTO;
import com.nju.partner.dto.PostQueryDTO;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.PostMapper;
import com.nju.partner.service.FavoriteService;
import com.nju.partner.service.PostService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public PostServiceImpl(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPost(PostCreateDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(dto.getTitle().trim());
        post.setType(dto.getType().trim());
        post.setDescription(dto.getDescription());
        post.setLocation(dto.getLocation());
        post.setActivityTime(dto.getActivityTime());
        post.setNeedCount(dto.getNeedCount());
        post.setCurrentCount(0);
        post.setCampus(dto.getCampus().trim());
        post.setGradeLimit(dto.getGradeLimit());
        post.setMajorLimit(dto.getMajorLimit());
        post.setContact(dto.getContact());
        post.setStatus(0);
        this.save(post);
    }

    @Override
    public IPage<PostVO> queryPosts(PostQueryDTO query) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getCampus())) {
            wrapper.eq(Post::getCampus, query.getCampus().trim());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(Post::getType, query.getType().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Post::getStatus, query.getStatus());
        }
        if (query.getUserId() != null) {
            wrapper.eq(Post::getUserId, query.getUserId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(Post::getTitle, keyword)
                    .or()
                    .like(Post::getDescription, keyword));
        }

        wrapper.orderByDesc(Post::getCreatedTime);

        Page<Post> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<Post> postPage = this.page(page, wrapper);

        return postPage.convert(this::toPostVO);
    }

    @Override
    public PostVO getPostDetail(Long postId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        return toPostVO(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long postId, PostCreateDTO dto) {
        Post post = checkOwner(postId);

        post.setTitle(dto.getTitle().trim());
        post.setType(dto.getType().trim());
        post.setDescription(dto.getDescription());
        post.setLocation(dto.getLocation());
        post.setActivityTime(dto.getActivityTime());
        post.setNeedCount(dto.getNeedCount());
        post.setCampus(dto.getCampus().trim());
        post.setGradeLimit(dto.getGradeLimit());
        post.setMajorLimit(dto.getMajorLimit());
        post.setContact(dto.getContact());
        this.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closePost(Long postId) {
        Post post = checkOwner(postId);
        if (post.getStatus() != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "只有招募中的帖子才能关闭");
        }
        post.setStatus(2);
        this.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId) {
        checkOwner(postId);
        this.removeById(postId);
    }

    private Post checkOwner(Long postId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Post post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权操作该帖子");
        }
        return post;
    }

    private PostVO toPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setType(post.getType());
        vo.setDescription(post.getDescription());
        vo.setLocation(post.getLocation());
        vo.setActivityTime(post.getActivityTime());
        vo.setNeedCount(post.getNeedCount());
        vo.setCurrentCount(post.getCurrentCount());
        vo.setCampus(post.getCampus());
        vo.setGradeLimit(post.getGradeLimit());
        vo.setMajorLimit(post.getMajorLimit());
        vo.setContact(post.getContact());
        vo.setStatus(post.getStatus());
        vo.setCreatedTime(post.getCreatedTime());
        vo.setUpdatedTime(post.getUpdatedTime());

        User user = userService.getById(post.getUserId());
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
            vo.setPublisher(userVO);
        }

        Long currentUserId = BaseContext.getCurrentUserId();
        if (currentUserId != null) {
            vo.setFavorited(favoriteService.isFavorited(currentUserId, post.getId()));
        }

        return vo;
    }
}
