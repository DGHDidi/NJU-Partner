package com.nju.partner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Favorite;
import com.nju.partner.vo.PostVO;

public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long postId);

    void removeFavorite(Long postId);

    IPage<PostVO> getMyFavorites(Integer pageNum, Integer pageSize);

    boolean isFavorited(Long userId, Long postId);
}
