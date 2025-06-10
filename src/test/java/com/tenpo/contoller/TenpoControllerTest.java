package com.tenpo.contoller;

import com.tenpo.interceptor.RateLimitInterceptor;
import com.tenpo.model.DinamicAverageResponse;
import com.tenpo.model.HistoryAverage;
import com.tenpo.repositoryredis.RedisRepository;
import com.tenpo.service.TenpoServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
public class TenpoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenpoServiceImpl tenpoServiceImpl;

    @MockBean
    private RedisRepository redisRepository;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    @Test
    void getDinamicAverageReturnsCorrectValue() throws Exception {

        DinamicAverageResponse response = new DinamicAverageResponse();
        response.setResult(BigDecimal.valueOf(5.0));
        when(tenpoServiceImpl.getDinamicAverage(Mockito.any(), Mockito.any()))
                .thenReturn(response);

        when(rateLimitInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(true);

        String jsonBody = """
        {
            "num1": 4,
            "num2": 6
        }
        """;

        mockMvc.perform(post("/v1/dinamic-average")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5.0));;
    }

    @Test
    void getDinamicAverageWithNegativeNumbersReturnsBadRequest() throws Exception {
        when(rateLimitInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(true);

        String jsonBody = """
        {
            "num1": -4,
            "num2": 6
        }
        """;

        mockMvc.perform(post("/v1/dinamic-average")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDinamicAverageReturnsTooManyRequest() throws Exception {
        RedisRepository redisRepository = mock(RedisRepository.class);
        when(redisRepository.isAllowed(anyString(), anyInt()))
                .thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v1/dinamic-average");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RateLimitInterceptor interceptor = new RateLimitInterceptor(redisRepository);
        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(429, response.getStatus());
    }

    @Test
    void getHistoryListReturnsData() throws Exception {
        List<HistoryAverage> mockList = List.of(new HistoryAverage(1L, LocalDateTime.now(), "/v1/dinamic-average", "input", "output"));

        when(rateLimitInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(true);

        when(tenpoServiceImpl.getHistoryList(anyInt(), anyInt())).thenReturn(mockList);

        mockMvc.perform(get("/v1/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getHistoryReturnsNotFound() throws Exception {
        when(rateLimitInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()))
                .thenReturn(true);
        when(tenpoServiceImpl.getHistoryList(anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/v1/history"))
                .andExpect(status().isNotFound());
    }
}
