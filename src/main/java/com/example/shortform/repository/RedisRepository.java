package com.example.shortform.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@RequiredArgsConstructor
@Repository
public class RedisRepository {
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashEnterInfo;

    public static final String ENTER_INFO = "ENTER_INFO";

    public void setUserEnterInfo(String sessionId, String roomId) {
        hashEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    public String getUserEnterRoomId(String sessionId) {
        return hashEnterInfo.get(ENTER_INFO, sessionId);
    }

    public void removeUserEnterInfo(String sessionId) {
        hashEnterInfo.delete(ENTER_INFO, sessionId);
    }
}
