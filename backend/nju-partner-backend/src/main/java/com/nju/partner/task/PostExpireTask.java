package com.nju.partner.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nju.partner.entity.Post;
import com.nju.partner.mapper.PostMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PostExpireTask {

    private final PostMapper postMapper;

    public PostExpireTask(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @PostConstruct
    public void expireOnStartup() {
        expireRecruitingPosts();
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void expireRecruitingPosts() {
        int count = postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .set(Post::getStatus, 3)
                .eq(Post::getStatus, 0)
                .isNotNull(Post::getActivityTime)
                .lt(Post::getActivityTime, LocalDateTime.now()));
        if (count > 0) {
            log.info("Expired {} recruiting posts", count);
        }
    }
}
