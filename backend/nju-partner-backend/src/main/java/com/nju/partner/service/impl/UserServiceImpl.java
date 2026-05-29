package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.UserMapper;
import com.nju.partner.service.UserService;
import com.nju.partner.utils.JwtUtils;
import com.nju.partner.utils.PasswordUtils;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;

    public UserServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterDTO dto) {
        String username = normalize(dto.getUsername());
        String password = normalize(dto.getPassword());
        String nickname = normalize(dto.getNickname());
        String campus = normalizeCampus(dto.getCampus());
        String grade = normalizeGrade(dto.getGrade());
        String major = normalize(dto.getMajor());

        if (campus == null || campus.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "校区不能为空");
        }
        if (grade == null || grade.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "年级不能为空");
        }

        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        User existingUser = this.getOne(query);
        if (existingUser != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encrypt(password));
        user.setNickname(nickname);
        user.setCampus(campus);
        user.setGrade(grade);
        user.setMajor(major);
        user.setRole("USER");
        user.setStatus(1);
        this.save(user);
    }

    @Override
    public LoginVO login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, dto.getUsername());
        User user = this.getOne(query);
        if (user == null || !PasswordUtils.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        String token = jwtUtils.generateToken(user.getId());
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(toUserVO(user));
        return loginVO;
    }

    @Override
    public UserVO getCurrentUserProfile() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return toUserVO(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setCampus(user.getCampus());
        vo.setGrade(user.getGrade());
        vo.setMajor(user.getMajor());
        vo.setRole(user.getRole());
        return vo;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeCampus(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        normalized = normalized.replaceAll("\\s+", "");
        if (normalized.contains("仙林")) return "仙林校区";
        if (normalized.contains("鼓楼")) return "鼓楼校区";
        if (normalized.contains("浦口")) return "浦口校区";
        if (normalized.contains("苏州")) return "苏州校区";
        return normalized;
    }

    private String normalizeGrade(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.replaceAll("\\s+", "");
    }
}

