package com.nju.partner.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.dto.UserUpdateDTO;
import com.nju.partner.entity.User;
import com.nju.partner.vo.ApplicationVO;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;

public interface UserService extends IService<User> {

    void register(UserRegisterDTO dto);

    LoginVO login(UserLoginDTO dto);

    UserVO getCurrentUserProfile();

    UserVO updateCurrentUserProfile(UserUpdateDTO dto);

    IPage<PostVO> getMyPosts(Integer pageNum, Integer pageSize);

    IPage<ApplicationVO> getMyApplications(Integer pageNum, Integer pageSize);

    IPage<PostVO> getMyFavorites(Integer pageNum, Integer pageSize);
}
