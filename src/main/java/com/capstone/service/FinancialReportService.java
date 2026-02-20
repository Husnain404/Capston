package com.capstone.service;

import com.capstone.dto.FinancialReportDto;
import com.capstone.model.FinancialReport;
import com.capstone.repository.FinancialReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialReportService {

    private final FinancialReportRepository repository;

    public List<FinancialReportDto> getDataForExcelFile(String year)
    {
        List<FinancialReportDto> dtos = new ArrayList<>();
        List<FinancialReport> financialReports = repository.findFinancialReportByYear(year);
        String[] months = {year+"01",year+"02",year+"03",year+"04",year+"05",year+"06",
                year+"07",year+"08",year+"09",year+"10",year+"11",year+"12"};
        for (int i = 0; i < months.length; i++) {
            List<FinancialReport> month = repository.findFinancialReportByYear(months[i]);
            FinancialReportDto financialReportDto = new FinancialReportDto();

            Double asset = month.stream()
                    .mapToDouble(FinancialReport::getAssets)
                    .sum();
            financialReportDto.setAssets(asset);

            Double liability = month.stream()
                    .mapToDouble(FinancialReport::getLiabilities)
                    .sum();
            financialReportDto.setLiability(liability);

            Double equity = month.stream()
                    .mapToDouble(FinancialReport::getEquity)
                    .sum();
            financialReportDto.setEquity(equity);

            Double revenue = month.stream()
                    .mapToDouble(FinancialReport::getRevenue)
                    .sum();
            financialReportDto.setRevenue(revenue);

            Double expenses = month.stream()
                    .mapToDouble(FinancialReport::getExpenses)
                    .sum();
            financialReportDto.setExpenses(expenses);

            Double profit = revenue-expenses;

            if (revenue>expenses)
            {
                financialReportDto.setNetProfit(profit);
                financialReportDto.setNetLoss(0.0);
            }else {
                financialReportDto.setNetProfit(0.0);
                financialReportDto.setNetLoss(Math.abs(profit));
            }
            dtos.add(financialReportDto);

        }
        return dtos;
    }
    public Workbook generateFinancialReport(List<FinancialReportDto> financialReports){
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Report");

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        String[] fields = {"Assets", "Liabilities", "Equity", "Revenue", "Expenses", "Net Profit", "Net Loss"};

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("");
        for (int i = 0; i < financialReports.size(); i++) {
            headerRow.createCell(i + 1).setCellValue(months[i]);
        }

        for (int i = 0; i < fields.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(fields[i]);

            for (int j = 0; j < financialReports.size(); j++) {
                FinancialReportDto report = financialReports.get(j);
                switch (i) {
                    case 0: row.createCell(j + 1).setCellValue(report.getAssets()); break;
                    case 1: row.createCell(j + 1).setCellValue(report.getLiability()); break;
                    case 2: row.createCell(j + 1).setCellValue(report.getEquity()); break;
                    case 3: row.createCell(j + 1).setCellValue(report.getRevenue()); break;
                    case 4: row.createCell(j + 1).setCellValue(report.getExpenses()); break;
                    case 5: row.createCell(j + 1).setCellValue(report.getNetProfit()); break;
                    case 6: row.createCell(j + 1).setCellValue(report.getNetLoss()); break;
                }
            }
        }

        return workbook;
    }

}
