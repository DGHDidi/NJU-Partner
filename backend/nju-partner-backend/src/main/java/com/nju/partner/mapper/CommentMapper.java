package com.nju.partner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.partner.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
