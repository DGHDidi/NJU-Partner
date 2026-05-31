package com.nju.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Favorite;
import com.nju.partner.vo.PostVO;
import java.util.List;

public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long postId);

    void removeFavorite(Long postId);

    boolean isFavorited(Long postId);

    // 我的收藏，返回对应的帖子视图
    List<PostVO> getMyFavorites();
}
