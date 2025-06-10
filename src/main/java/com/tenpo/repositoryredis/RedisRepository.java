package com.tenpo.repositoryredis;



import com.tenpo.exception.RedisRecoverException;
import com.tenpo.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RedisRepository {

    private static final Logger logger = LoggerFactory.getLogger(RedisRepository.class);

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${redis.default.ttl}")
    private long defaultRedisTtl;

    public RedisRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveIntoRedis(String key, BigDecimal value){
        try {
            logger.info("Saved in Redis: {} value: {}", key, value);
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value), defaultRedisTtl, TimeUnit.MINUTES);
            logger.info("Value successfully saved in Redis!");
        } catch (Exception e){
            logger.error("Error saving value in Redis: {}", e.getMessage());
        }

    }

    public BigDecimal getExternalValueFromRedis(String key){
        logger.info("Looking for key: {} in Redis.", key);
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null || value.isEmpty()) {
            logger.info("Key not found in Redis.");
            throw new RedisRecoverException("Value not found in Redis.");
        }
        logger.info("Retrieved value from Redis: {}", value);
        return BigDecimal.valueOf(Double.parseDouble(value));
    }

    public boolean isAllowed(String clientIp, int requestLimit) {
        String RATE_LIMIT_PREFIX = "rate_limit:";
        String key = RATE_LIMIT_PREFIX + clientIp;
        Long requestCount = stringRedisTemplate.opsForValue().increment(key, 1);
        if (requestCount == 1) {
            stringRedisTemplate.expire(key, Duration.ofMinutes(1));
        }
        return requestCount <= requestLimit;
    }
}
