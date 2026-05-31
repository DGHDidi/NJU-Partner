package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.dto.UserLoginDTO;
import com.nju.partner.dto.UserRegisterDTO;
import com.nju.partner.dto.UserUpdateDTO;
import com.nju.partner.entity.Application;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.ApplicationMapper;
import com.nju.partner.mapper.PostMapper;
import com.nju.partner.mapper.UserMapper;
import com.nju.partner.service.FavoriteService;
import com.nju.partner.service.UserService;
import com.nju.partner.utils.JwtUtils;
import com.nju.partner.utils.PasswordUtils;
import com.nju.partner.vo.ApplicationVO;
import com.nju.partner.vo.LoginVO;
import com.nju.partner.vo.PostVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;
    private final PostMapper postMapper;
    private final ApplicationMapper applicationMapper;
    private final FavoriteService favoriteService;

    public UserServiceImpl(JwtUtils jwtUtils,
                           PostMapper postMapper,
                           ApplicationMapper applicationMapper,
                           FavoriteService favoriteService) {
        this.jwtUtils = jwtUtils;
        this.postMapper = postMapper;
        this.applicationMapper = applicationMapper;
        this.favoriteService = favoriteService;
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
        User user = getCurrentUserEntity();
        return toUserVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateCurrentUserProfile(UserUpdateDTO dto) {
        User user = getCurrentUserEntity();

        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(normalize(dto.getNickname()));
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(normalize(dto.getAvatar()));
        }
        if (StringUtils.hasText(dto.getCampus())) {
            user.setCampus(normalizeCampus(dto.getCampus()));
        }
        if (StringUtils.hasText(dto.getGrade())) {
            user.setGrade(normalizeGrade(dto.getGrade()));
        }
        if (dto.getMajor() != null) {
            user.setMajor(normalize(dto.getMajor()));
        }

        this.updateById(user);
        return toUserVO(user);
    }

    @Override
    public IPage<PostVO> getMyPosts(Integer pageNum, Integer pageSize) {
        Long userId = currentUserId();
        Page<Post> page = postMapper.selectPage(new Page<>(safePageNum(pageNum), safePageSize(pageSize)),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getUserId, userId)
                        .orderByDesc(Post::getCreatedTime));
        return page.convert(this::toPostVO);
    }

    @Override
    public IPage<ApplicationVO> getMyApplications(Integer pageNum, Integer pageSize) {
        Long userId = currentUserId();
        Page<Application> page = applicationMapper.selectPage(new Page<>(safePageNum(pageNum), safePageSize(pageSize)),
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getUserId, userId)
                        .orderByDesc(Application::getCreatedTime));
        return page.convert(this::toMyApplicationVO);
    }

    @Override
    public IPage<PostVO> getMyFavorites(Integer pageNum, Integer pageSize) {
        return favoriteService.getMyFavorites(pageNum, pageSize);
    }

    private ApplicationVO toMyApplicationVO(Application application) {
        ApplicationVO vo = new ApplicationVO();
        vo.setId(application.getId());
        vo.setPostId(application.getPostId());
        vo.setUserId(application.getUserId());
        vo.setMessage(application.getMessage());
        vo.setStatus(application.getStatus());
        vo.setCreatedTime(application.getCreatedTime());
        vo.setUpdatedTime(application.getUpdatedTime());

        Post post = postMapper.selectById(application.getPostId());
        if (post != null) {
            vo.setPostTitle(post.getTitle());
            vo.setPostStatus(post.getStatus());
        }
        return vo;
    }

    private PostVO toPostVO(Post post) {
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

        User publisher = this.getById(post.getUserId());
        if (publisher != null) {
            vo.setPublisher(toUserVO(publisher));
        }

        Long currentUserId = BaseContext.getCurrentUserId();
        if (currentUserId != null) {
            vo.setFavorited(favoriteService.isFavorited(currentUserId, post.getId()));
        }
        return vo;
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
        vo.setStatus(user.getStatus());
        return vo;
    }

    private User getCurrentUserEntity() {
        Long userId = currentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return user;
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
