package com.tenpo.externalservice;


import com.tenpo.model.ExternalServiceClientResponseDto;
import com.tenpo.repositoryredis.RedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class ExternalServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceClient.class);

    @Value("${retry.maxAttempts}")
    private String attempts;

    @Value("${externalServiceUrl}")
    private String externalServiceUrl;

    private final RedisRepository redisRepository;

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalServiceClient(RedisRepository redisRepository, RestTemplate restTemplate) {
        this.redisRepository = redisRepository;
        this.restTemplate = restTemplate;
    }

    @Retryable(maxAttemptsExpression = "${retry.maxAttempts}")
    public BigDecimal getValueFromExternalService() {
        logger.info("----- Getting value from External Service -----");

        int randomValue = new Random().nextInt(99);
        String externalServiceFullUrl = externalServiceUrl + randomValue;

        try {
            ResponseEntity<ExternalServiceClientResponseDto> response =
                    restTemplate.getForEntity(externalServiceFullUrl, ExternalServiceClientResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                BigDecimal value = BigDecimal.valueOf(response.getBody().getId());
                redisRepository.saveIntoRedis("externalValue", value);
                return value;
            } else {
                throw new RuntimeException("Unexpected response from external service");
            }
        } catch (Exception e) {
            logger.error("Error contacting external service", e);
            throw e;
        }
    }

    @Recover
    public BigDecimal recoverFromFailure(Exception e) {
        logger.info("error after {} attempts. Looking into Redis...", attempts);
        return redisRepository.getExternalValueFromRedis("externalValue");
    }

}