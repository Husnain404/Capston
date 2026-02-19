package com.capstone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TrialBalanceEntryDto {

    private String code;
    private String accountName;
    private BigDecimal debit;
    private BigDecimal credit;


}
