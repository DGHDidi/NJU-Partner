package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Favorite;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.FavoriteMapper;
import com.nju.partner.service.FavoriteService;
import com.nju.partner.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.nju.partner.vo.PostVO;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final PostService postService;

    public FavoriteServiceImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long postId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (postService.getById(postId) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        long count = this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "已收藏过该帖子");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        this.save(favorite);
    }

    @Override
    public List<PostVO> getMyFavorites() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        List<Favorite> favs = this.list(new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreatedTime));
        return favs.stream().map(f -> postService.getPostDetail(f.getPostId())).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long postId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId));
    }

    @Override
    public boolean isFavorited(Long postId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            return false;
        }
        return this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId)) > 0;
    }
}
