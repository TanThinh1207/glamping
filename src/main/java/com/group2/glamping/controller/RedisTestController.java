package com.group2.glamping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/redis/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
        return "Value set successfully!";
    }

    @GetMapping("/redis/get")
    public String getValue(@RequestParam String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? "Value: " + value : "Key not found!";
    }

    @GetMapping("/redis/delete")
    public String deleteValue(@RequestParam String key) {
        Boolean result = redisTemplate.delete(key);
        return result != null && result ? "Key deleted successfully!" : "Key not found or delete failed!";
    }
} 
