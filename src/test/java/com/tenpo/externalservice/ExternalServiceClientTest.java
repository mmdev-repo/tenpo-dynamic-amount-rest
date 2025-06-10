package com.tenpo.externalservice;

import com.tenpo.exception.RedisRecoverException;
import com.tenpo.model.ExternalServiceClientResponseDto;
import com.tenpo.repositoryredis.RedisRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExternalServiceClientTest {

    @MockBean
    private RedisRepository redisRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("retry.maxAttempts", () -> "3");
        registry.add("externalServiceUrl", () -> "http://dummy-url.com/service/");
    }

    @Test
    public void testGetValueFromExternalServiceSuccess() {
        ExternalServiceClientResponseDto responseDto = mock(ExternalServiceClientResponseDto.class);
        when(responseDto.getId()).thenReturn(42L);

        ResponseEntity<ExternalServiceClientResponseDto> responseEntity =
                new ResponseEntity<>(responseDto, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(ExternalServiceClientResponseDto.class)))
                .thenReturn(responseEntity);

        BigDecimal result = externalServiceClient.getValueFromExternalService();

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(42), result);

        verify(redisRepository).saveIntoRedis("externalValue", BigDecimal.valueOf(42));
    }

    @Test
    public void testGetValueFromExternalServiceExceptionRecover() {
        when(restTemplate.getForEntity(anyString(), eq(ExternalServiceClientResponseDto.class)))
                .thenThrow(new RuntimeException("Service down"));

        when(redisRepository.getExternalValueFromRedis("externalValue"))
                .thenReturn(BigDecimal.valueOf(99));

        BigDecimal result = externalServiceClient.getValueFromExternalService();

        assertEquals(BigDecimal.valueOf(99), result);
        verify(redisRepository).getExternalValueFromRedis("externalValue");
    }

    @Test
    public void testGetValueFromExternalServiceException() {
        when(restTemplate.getForEntity(anyString(), eq(ExternalServiceClientResponseDto.class)))
                .thenThrow(new RuntimeException("Unexpected response from external service"));

        when(redisRepository.getExternalValueFromRedis("externalValue"))
                .thenThrow(new RedisRecoverException("Value not found in Redis."));

        RedisRecoverException exception = assertThrows(RedisRecoverException.class, () -> {
            externalServiceClient.getValueFromExternalService();
        });

        assertEquals("Value not found in Redis.", exception.getMessage());
    }
}
