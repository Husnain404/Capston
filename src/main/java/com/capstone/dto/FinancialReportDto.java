package com.capstone.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDto {

    private Double assets;
    private Double liability;
    private Double equity;
    private Double revenue;
    private Double expenses;


    private Double netProfit;
    private Double netLoss;


}
