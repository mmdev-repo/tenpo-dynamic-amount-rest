package com.tenpo.model;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InputValueDto {

    @DecimalMin(value = "0.0", inclusive = true, message = "num1 must be greater than or equal to 0")
    private BigDecimal num1;

    @DecimalMin(value = "0.0", inclusive = true, message = "num1 must be greater than or equal to 0")
    private BigDecimal num2;
}
