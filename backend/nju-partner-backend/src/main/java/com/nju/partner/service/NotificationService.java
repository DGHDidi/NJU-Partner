package com.nju.partner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nju.partner.entity.Notification;
import com.nju.partner.vo.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<Notification> {

    void createNotification(Long userId, String type, String title, String content,
                            Long postId, Long commentId, Long applicationId);

    List<NotificationVO> getMyNotifications();

    long countUnread();

    void markRead(Long id);

    void markAllRead();
}
