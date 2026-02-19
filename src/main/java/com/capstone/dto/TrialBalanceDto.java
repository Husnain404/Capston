package com.capstone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TrialBalanceDto {

    private String fileName;
    private List<TrialBalanceEntryDto> trialBalanceEntryDto;


}
