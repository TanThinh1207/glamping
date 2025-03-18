package com.group2.glamping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    @DeleteMapping("/cache")
    public ResponseEntity<String> clearAllCache() {
        if (redisTemplate.getConnectionFactory() != null) {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                return ResponseEntity.ok("All caches have been cleared successfully.");
            } else {
                return ResponseEntity.ok("No caches to clear.");
            }
        } else {
            return ResponseEntity.status(500).body("Redis connection is not available.");
        }
    }
} 
