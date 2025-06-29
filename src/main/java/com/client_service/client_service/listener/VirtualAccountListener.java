package com.client_service.client_service.listener;

import com.client_service.client_service.service.DataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class VirtualAccountListener implements MessageListener {

    @Autowired
    DataLoader dataLoader;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Received Redis VA update, refreshing prefixMap... ");
        dataLoader.loadPrefixMapFromRedis();
    }
}
