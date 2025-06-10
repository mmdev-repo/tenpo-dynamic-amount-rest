package com.tenpo.controller;

import com.tenpo.aop.SaveRequestInfo;
import com.tenpo.exception.ResourceNotFoundException;
import com.tenpo.model.DinamicAverageResponse;
import com.tenpo.model.HistoryAverage;
import com.tenpo.model.InputValueDto;
import com.tenpo.service.TenpoServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "dinamic-average", description = "dinamic-average API")
public class TenpoController {

    @Autowired
    private TenpoServiceImpl tenpoServiceImpl;


    @PostMapping("/dinamic-average")
    @SaveRequestInfo
    public ResponseEntity<DinamicAverageResponse> getDinamicAverage(@RequestBody @Valid InputValueDto inputValueDto) {
        DinamicAverageResponse result = tenpoServiceImpl.getDinamicAverage(inputValueDto.getNum1(), inputValueDto.getNum2());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    @GetMapping("/history")
    public List<HistoryAverage> getHistoryList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<HistoryAverage> list = tenpoServiceImpl.getHistoryList(page, size);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Registers not found.");
        }
        return list;
    }
}
