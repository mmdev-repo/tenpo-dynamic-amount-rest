package com.tenpo.interceptor;

import com.tenpo.repositoryredis.RedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${requestLimit}")
    private int requestLimit;

    private final RedisRepository redisRepository;

    public RateLimitInterceptor(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        boolean isAllowed = redisRepository.isAllowed(clientIp, requestLimit);
        if (isAllowed) {
            return true;
        }
        response.setStatus(429);
        response.setContentType("text/plain");
        response.getWriter().write("Too many requests - Try again later");
        return false;
    }
}
