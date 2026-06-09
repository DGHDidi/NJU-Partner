package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.dto.PostCreateDTO;
import com.nju.partner.dto.PostQueryDTO;
import com.nju.partner.entity.Application;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.mapper.ApplicationMapper;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.PostMapper;
import com.nju.partner.service.FavoriteService;
import com.nju.partner.service.PostService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final ApplicationMapper applicationMapper;

    public PostServiceImpl(UserService userService, FavoriteService favoriteService, ApplicationMapper applicationMapper) {
        this.userService = userService;
        this.favoriteService = favoriteService;
        this.applicationMapper = applicationMapper;
    }

    @Override
    public List<PostVO> getMyPosts() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        List<Post> posts = this.list(new LambdaQueryWrapper<Post>().eq(Post::getUserId, userId)
                .orderByDesc(Post::getCreatedTime));

        Map<Long, User> userMap = userService.listByIds(Collections.singletonList(userId))
                .stream().collect(Collectors.toMap(User::getId, item -> item));

        return posts.stream()
                .map(post -> toPostVO(post, userMap, Collections.emptyMap()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPost(PostCreateDTO dto) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        validatePostPayload(dto);

        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(dto.getTitle().trim());
        post.setType(dto.getType().trim());
        post.setDescription(dto.getDescription());
        post.setLocation(dto.getLocation());
        post.setActivityTime(dto.getActivityTime());
        post.setNeedCount(dto.getNeedCount());
        post.setCurrentCount(1);
        post.setCampus(dto.getCampus().trim());
        post.setGradeLimit(dto.getGradeLimit());
        post.setMajorLimit(dto.getMajorLimit());
        post.setContact(dto.getContact().trim());
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
        if (StringUtils.hasText(query.getGrade())) {
            String grade = query.getGrade().trim();
            wrapper.and(w -> w.isNull(Post::getGradeLimit)
                    .or()
                    .eq(Post::getGradeLimit, "")
                    .or()
                    .eq(Post::getGradeLimit, "不限")
                    .or()
                    .like(Post::getGradeLimit, grade));
        }
        if (query.getStartTime() != null) {
            wrapper.ge(Post::getActivityTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(Post::getActivityTime, query.getEndTime());
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
        return buildPostPage(postPage);
    }

    @Override
    public PostVO getPostDetail(Long postId) {
        Post post = this.getById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        return toPostVO(post, Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePost(Long postId, PostCreateDTO dto) {
        Post post = checkOwner(postId);
        validatePostPayload(dto);

        post.setTitle(dto.getTitle().trim());
        post.setType(dto.getType().trim());
        post.setDescription(dto.getDescription());
        post.setLocation(dto.getLocation());
        post.setActivityTime(dto.getActivityTime());
        post.setNeedCount(dto.getNeedCount());
        post.setCampus(dto.getCampus().trim());
        post.setGradeLimit(dto.getGradeLimit());
        post.setMajorLimit(dto.getMajorLimit());
        post.setContact(dto.getContact().trim());
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

    private IPage<PostVO> buildPostPage(Page<Post> postPage) {
        List<Post> posts = postPage.getRecords();
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, User> userMap = userIds.isEmpty() ? Collections.emptyMap() :
                userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, item -> item));
        Map<Long, Boolean> favoriteMap = favoriteService.getFavoritedStatusMap(BaseContext.getCurrentUserId(), postIds);

        Page<PostVO> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(posts.stream().map(post -> toPostVO(post, userMap, favoriteMap)).toList());
        return result;
    }

    private PostVO toPostVO(Post post, Map<Long, User> userMap, Map<Long, Boolean> favoriteMap) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setType(post.getType());
        vo.setDescription(post.getDescription());
        vo.setLocation(post.getLocation());
        vo.setActivityTime(post.getActivityTime());
        vo.setNeedCount(post.getNeedCount());
        vo.setCurrentCount(resolveCurrentCount(post));
        vo.setCampus(post.getCampus());
        vo.setGradeLimit(post.getGradeLimit());
        vo.setMajorLimit(post.getMajorLimit());
        vo.setContact(post.getContact());
        vo.setStatus(post.getStatus());
        vo.setCreatedTime(post.getCreatedTime());
        vo.setUpdatedTime(post.getUpdatedTime());

        User user = userMap.get(post.getUserId());
        if (user == null) {
            user = userService.getById(post.getUserId());
        }
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

        if (!favoriteMap.isEmpty()) {
            vo.setFavorited(favoriteMap.getOrDefault(post.getId(), false));
        } else {
            Long currentUserId = BaseContext.getCurrentUserId();
            vo.setFavorited(currentUserId != null && favoriteService.isFavorited(currentUserId, post.getId()));
        }

        return vo;
    }

    private int resolveCurrentCount(Post post) {
        Long approvedCount = applicationMapper.selectCount(new LambdaQueryWrapper<Application>()
                .eq(Application::getPostId, post.getId())
                .eq(Application::getStatus, 1));
        return approvedCount.intValue() + 1;
    }

    private void validatePostPayload(PostCreateDTO dto) {
        if (dto.getActivityTime() != null && !dto.getActivityTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "活动时间必须晚于当前时间");
        }
    }
}
