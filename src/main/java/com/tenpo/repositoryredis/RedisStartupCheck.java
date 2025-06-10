package com.tenpo.repositoryredis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupCheck implements ApplicationRunner {


private static final Logger logger = LoggerFactory.getLogger(RedisStartupCheck.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            redisTemplate.opsForValue().set("testValue", "testValue");
            String value = redisTemplate.opsForValue().get("testKey");
            logger.info("Redis connection successful: {}", value);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Redis connection Error: {}", e.getMessage());
        }
    }

}
