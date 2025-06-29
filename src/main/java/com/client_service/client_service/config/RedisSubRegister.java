package com.client_service.client_service.config;

import com.client_service.client_service.listener.VirtualAccountListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import static com.api.common.constant.RedisConstant.REDIS_PREFIX_CACHE;


@Slf4j
@Component
public class RedisSubRegister {

    @Autowired
    private RedisMessageListenerContainer container;

    @Autowired
    private VirtualAccountListener virtualAccountListener;

    @PostConstruct
    public void setupListener() {
        log.info("RedisSubRegistrar initialized, registering listener...");
        System.out.println("Redis listener container initialized");
        container.addMessageListener(virtualAccountListener, new ChannelTopic(REDIS_PREFIX_CACHE.getName()));
    }
}
