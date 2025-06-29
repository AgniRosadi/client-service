package com.client_service.client_service.service;

import com.api.common.dto.PrefixDataRes;
import com.api.common.dto.VirtualAccountReq;
import com.api.common.dto.VirtualAccountRes;
import com.client_service.client_service.exception.PrefixNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.TreeMap;

@Slf4j
@Service
public class VirtualAccountService {
    @Autowired
    DataLoader dataLoader;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TreeMap<String, PrefixDataRes> prefixMap;

    public PrefixDataRes checkPrefix(VirtualAccountReq request) {
        VirtualAccountRes response = new VirtualAccountRes();
        if (request.getVirtualAccount() == null || request.getVirtualAccount().isEmpty()) {
            throw new IllegalArgumentException("VA number invalid");
        }

        if (prefixMap.isEmpty()) {
            dataLoader.loadPrefixMapFromRedis();

            if (prefixMap.isEmpty()) {
                dataLoader.loadPrefixMapFromDb();
            }
        }

        String prefixCandidate = request.getVirtualAccount().substring(0, Math.min(6, request.getVirtualAccount().length()));
        String matchKey = prefixMap.floorKey(prefixCandidate);

        if (matchKey != null && prefixCandidate.startsWith(matchKey)) {
            return prefixMap.get(matchKey);
        } else {
            throw new PrefixNotFoundException("Prefix not found for VA: " + request.getVirtualAccount());
        }
    }

}

