package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Application;
import com.nju.partner.entity.Post;
import com.nju.partner.entity.User;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.ApplicationMapper;
import com.nju.partner.service.ApplicationService;
import com.nju.partner.service.PostService;
import com.nju.partner.service.UserService;
import com.nju.partner.vo.ApplicationVO;
import com.nju.partner.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    private final PostService postService;
    private final UserService userService;

    public ApplicationServiceImpl(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(Long postId, String message) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        if (post.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "不能报名自己发布的帖子");
        }
        if (post.getStatus() != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "只有招募中的帖子可以报名");
        }

        long count = this.count(new LambdaQueryWrapper<Application>()
                .eq(Application::getPostId, postId)
                .eq(Application::getUserId, userId));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "你已经报名过该帖子，不能重复报名");
        }

        Application application = new Application();
        application.setPostId(postId);
        application.setUserId(userId);
        application.setMessage(message);
        application.setStatus(0);
        this.save(application);
    }

    @Override
    public List<ApplicationVO> getApplications(Long postId) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只有帖子发布者可以查看报名列表");
        }

        List<Application> applications = this.list(new LambdaQueryWrapper<Application>()
                .eq(Application::getPostId, postId)
                .orderByDesc(Application::getCreatedTime));

        return applications.stream().map(this::toApplicationVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passApplication(Long applicationId) {
        Application application = checkOwnershipAndGet(applicationId, true);
        if (application.getStatus() != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "只能通过待审核的报名");
        }

        application.setStatus(1);
        this.updateById(application);

        Post post = postService.getById(application.getPostId());
        int newCount = post.getCurrentCount() + 1;
        post.setCurrentCount(newCount);
        if (newCount >= post.getNeedCount()) {
            post.setStatus(1);
        }
        postService.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectApplication(Long applicationId) {
        Application application = checkOwnershipAndGet(applicationId, true);
        if (application.getStatus() != 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "只能拒绝待审核的报名");
        }
        application.setStatus(2);
        this.updateById(application);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelApplication(Long applicationId) {
        Application application = checkOwnershipAndGet(applicationId, false);
        if (application.getStatus() == 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "已通过的报名不能取消");
        }
        if (application.getStatus() == 3) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "报名已经取消过了");
        }
        application.setStatus(3);
        this.updateById(application);
    }

    private Application checkOwnershipAndGet(Long applicationId, boolean checkPostOwner) {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Application application = this.getById(applicationId);
        if (application == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "报名记录不存在");
        }
        if (checkPostOwner) {
            Post post = postService.getById(application.getPostId());
            if (!post.getUserId().equals(userId)) {
                throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只有帖子发布者可以操作报名");
            }
        } else {
            if (!application.getUserId().equals(userId)) {
                throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "只能操作自己的报名");
            }
        }
        return application;
    }

    private ApplicationVO toApplicationVO(Application application) {
        ApplicationVO vo = new ApplicationVO();
        vo.setId(application.getId());
        vo.setPostId(application.getPostId());
        vo.setUserId(application.getUserId());
        vo.setMessage(application.getMessage());
        vo.setStatus(application.getStatus());
        vo.setCreatedTime(application.getCreatedTime());
        vo.setUpdatedTime(application.getUpdatedTime());
        Post post = postService.getById(application.getPostId());
        if (post != null) {
            vo.setPostTitle(post.getTitle());
            vo.setPostStatus(post.getStatus());
        }

        User user = userService.getById(application.getUserId());
        if (user != null) {
            UserVO userVO = new UserVO();
            userVO.setId(user.getId());
            userVO.setUsername(user.getUsername());
            userVO.setNickname(user.getNickname());
            userVO.setAvatar(user.getAvatar());
            userVO.setCampus(user.getCampus());
            userVO.setGrade(user.getGrade());
            userVO.setMajor(user.getMajor());
            userVO.setRole(user.getRole());
            vo.setApplicant(userVO);
        }
        return vo;
    }
}
