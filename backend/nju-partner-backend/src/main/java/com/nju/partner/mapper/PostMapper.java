package com.nju.partner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.partner.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
