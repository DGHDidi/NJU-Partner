package com.nju.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Application;
import com.nju.partner.vo.ApplicationVO;

import java.util.List;

public interface ApplicationService extends IService<Application> {

    void apply(Long postId, String message);

    List<ApplicationVO> getApplications(Long postId);

    ApplicationVO getMyApplication(Long postId);

    List<ApplicationVO> getApprovedMembers(Long postId);

    void passApplication(Long applicationId);

    void rejectApplication(Long applicationId);

    void cancelApplication(Long applicationId);

    // 我报名的记录
    List<ApplicationVO> getMyApplications();
}
