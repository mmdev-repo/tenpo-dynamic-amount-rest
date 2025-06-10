package com.tenpo.service;

import com.tenpo.externalservice.ExternalServiceClient;
import com.tenpo.model.DinamicAverageResponse;
import com.tenpo.model.HistoryAverage;
import com.tenpo.repositoryjpa.TenpoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TenpoServiceImpl {

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private TenpoRepository tenpoRepository;

    public DinamicAverageResponse getDinamicAverage(BigDecimal num1, BigDecimal num2) {
        BigDecimal externalValue = externalServiceClient.getValueFromExternalService();
        BigDecimal baseSum = num1.add(num2);
        BigDecimal percentage = baseSum.multiply(externalValue).divide(BigDecimal.valueOf(100));
        DinamicAverageResponse response = new DinamicAverageResponse();
        response.setResult(baseSum.add(percentage));
        return response;
    }

    public List<HistoryAverage> getHistoryList(int page, int size){
        Pageable pageRepository = PageRequest.of(page, size);
        Page<HistoryAverage> pageResult = tenpoRepository.findAll(pageRepository);
        return pageResult.toList();
    }

    @Async
    public void saveHistoryAverage(HistoryAverage historyAverage){
        tenpoRepository.save(historyAverage);
    }

}
