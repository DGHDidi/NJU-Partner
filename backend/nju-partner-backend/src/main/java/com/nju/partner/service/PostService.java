package com.nju.partner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.dto.PostCreateDTO;
import com.nju.partner.dto.PostQueryDTO;
import com.nju.partner.entity.Post;
import com.nju.partner.vo.PostVO;

public interface PostService extends IService<Post> {

    void createPost(PostCreateDTO dto);

    IPage<PostVO> queryPosts(PostQueryDTO query);

    PostVO getPostDetail(Long postId);

    void updatePost(Long postId, PostCreateDTO dto);

    void closePost(Long postId);

    void deletePost(Long postId);
}
