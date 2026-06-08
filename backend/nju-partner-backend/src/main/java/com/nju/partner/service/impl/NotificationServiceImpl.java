package com.nju.partner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nju.partner.common.BaseContext;
import com.nju.partner.common.ResultCode;
import com.nju.partner.entity.Notification;
import com.nju.partner.exception.BusinessException;
import com.nju.partner.mapper.NotificationMapper;
import com.nju.partner.service.NotificationService;
import com.nju.partner.vo.NotificationVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Override
    public void createNotification(Long userId, String type, String title, String content,
                                   Long postId, Long commentId, Long applicationId) {
        if (userId == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setPostId(postId);
        notification.setCommentId(commentId);
        notification.setApplicationId(applicationId);
        notification.setIsRead(0);
        this.save(notification);
    }

    @Override
    public List<NotificationVO> getMyNotifications() {
        Long userId = currentUserId();
        return this.list(new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreatedTime))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public long countUnread() {
        Long userId = currentUserId();
        return this.count(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
    }

    @Override
    public void markRead(Long id) {
        Long userId = currentUserId();
        Notification notification = this.getById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "通知不存在");
        }
        notification.setIsRead(1);
        this.updateById(notification);
    }

    @Override
    public void markAllRead() {
        Long userId = currentUserId();
        Notification notification = new Notification();
        notification.setIsRead(1);
        this.update(notification, new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
    }

    private NotificationVO toVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setUserId(notification.getUserId());
        vo.setType(notification.getType());
        vo.setTitle(notification.getTitle());
        vo.setContent(notification.getContent());
        vo.setPostId(notification.getPostId());
        vo.setCommentId(notification.getCommentId());
        vo.setApplicationId(notification.getApplicationId());
        vo.setIsRead(notification.getIsRead());
        vo.setCreatedTime(notification.getCreatedTime());
        return vo;
    }

    private Long currentUserId() {
        Long userId = BaseContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}
