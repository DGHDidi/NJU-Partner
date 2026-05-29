package com.nju.partner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nju.partner.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

