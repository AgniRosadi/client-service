package com.client_service.client_service.service;

import com.api.common.dto.PrefixDataRes;
import com.api.common.dto.VirtualAccountRes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.api.common.constant.RedisConstant.REDIS_PREFIX_CACHE;


@Service
@Slf4j
public class DataLoader {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TreeMap<String, PrefixDataRes> prefixMap;

    @Autowired
    private HashOperations<String, String, Object> redisOps;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ReentrantLock lock = new ReentrantLock();

    @Value("${url.subscriber-service}")
    String urlSubscriber;

    public void loadPrefixMapFromDb() {
        log.info("Start loadPrefixMapFromDb");
        lock.lock();
        try {
            String url = urlSubscriber + "/prefix/check";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Start load Subscriber: {}", response);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<PrefixDataRes> prefixDataList = objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });

                prefixMap.clear();

                for (PrefixDataRes data : prefixDataList) {
                    prefixMap.put(data.getPrefix(), data);
                    redisOps.put(REDIS_PREFIX_CACHE.getName(), data.getPrefix(), data);
                }

                log.info("Prefix map loaded from DB with {} entries", prefixMap.size());
            } else {
                log.warn("Failed to fetch prefix data from subscriber-service, status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Failed to load prefix map from DB", e);
        } finally {
            lock.unlock();
        }
    }

    public void loadPrefixMapFromRedis() {
        log.info("Start loadPrefixMapFromRedis");
        lock.lock();
        try {
            prefixMap.clear();
            Map<String, Object> cachedPrefixes = redisOps.entries(REDIS_PREFIX_CACHE.getName());
            log.info("get redis : {}", cachedPrefixes.size());
            cachedPrefixes.forEach((key, value) -> {
                if (value instanceof PrefixDataRes vaRes) {
                    prefixMap.put(key, vaRes);
                } else {
                    log.warn("Invalid type found in Redis for prefix: {}", key);
                }
            });
            log.info("Prefix map refreshed with {} entries", prefixMap.size());
        } catch (Exception e) {
            log.error("Failed to load prefix map from Redis", e);
        } finally {
            lock.unlock();
        }
    }
}
