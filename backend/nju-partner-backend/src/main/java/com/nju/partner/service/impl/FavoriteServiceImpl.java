package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Favorite;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.FavoriteMapper;
import com.nju.partner.mapper.PostMapper;
import com.nju.partner.service.FavoriteService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final PostMapper postMapper;
    private final UserService userService;

    public FavoriteServiceImpl(PostMapper postMapper, UserService userService) {
        this.postMapper = postMapper;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long postId) {
        Long userId = currentUserId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }

        long count = this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId));
        if (count > 0) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        this.save(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long postId) {
        Long userId = currentUserId();
        this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId));
    }

    @Override
    public IPage<PostVO> getMyFavorites(Integer pageNum, Integer pageSize) {
        Long userId = currentUserId();
        Page<Favorite> favoritePage = this.page(new Page<>(safePageNum(pageNum), safePageSize(pageSize)),
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedTime));

        List<Long> postIds = favoritePage.getRecords().stream().map(Favorite::getPostId).toList();
        if (postIds.isEmpty()) {
            Page<PostVO> empty = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>().in(Post::getId, postIds));
        Map<Long, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, item -> item));
        List<Long> userIds = posts.stream().map(Post::getUserId).distinct().toList();
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, item -> item));

        List<PostVO> records = postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(post -> toPostVO(post, userMap))
                .toList();

        Page<PostVO> result = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public boolean isFavorited(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return false;
        }
        return getFavoritedStatusMap(userId, List.of(postId)).getOrDefault(postId, false);
    }

    @Override
    public Map<Long, Boolean> getFavoritedStatusMap(Long userId, List<Long> postIds) {
        if (userId == null || postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Favorite> favorites = this.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .in(Favorite::getPostId, postIds));
        Map<Long, Boolean> map = new HashMap<>();
        for (Favorite favorite : favorites) {
            map.put(favorite.getPostId(), true);
        }
        return map;
    }

    private PostVO toPostVO(Post post, Map<Long, User> userMap) {
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

        User user = userMap.get(post.getUserId());
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

        vo.setFavorited(true);
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
