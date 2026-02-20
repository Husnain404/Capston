package com.capstone.service;

import com.capstone.exception.FileNameInvalidException;
import com.capstone.exception.TrialBalanceNotValidException;
import com.capstone.model.FinancialReport;
import com.capstone.model.TrialBalance;
import com.capstone.model.TrialBalanceEntry;
import com.capstone.repository.TrialBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrialBalanceService {

    private final TrialBalanceRepository repository;

    private static final String PREFIX = "TRIAL_BALANCE_";


    public void fileNameFileValidate(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        boolean isValid = true;
        String errorMassage = null;

        if (fileName == null || !fileName.startsWith(PREFIX)) {
            isValid = false;
            errorMassage = "fileName In Not valid";
        } else if (!fileName.endsWith(".xlsx")) {
            isValid = false;
            errorMassage = "fileName In Not valid";
        } else if (fileName.length() != PREFIX.length() + 13) {
            isValid = false;
            errorMassage = "fileName In Not valid";
        } else {
            String datePart = fileName.substring(PREFIX.length(), PREFIX.length() + 8);

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd")
                        .withResolverStyle(ResolverStyle.STRICT);
                LocalDate.parse(datePart, formatter);
            } catch (DateTimeParseException e) {
                isValid = false;
                errorMassage = e.getMessage();
            }
        }
        handelFile(file, isValid);
        if (!isValid) {
            throw new FileNameInvalidException(errorMassage);
        }
    }


    public void accountingTotalValidate(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        TrialBalance trialBalance = convertToObj(inputStream, fileName);
        boolean isValid = true;
        String errorMassage = null;
        try {
            List<TrialBalanceEntry> entries;
            entries = trialBalance.getTrailBalanceEntries();
            Double totalDebit = entries.stream()
                    .mapToDouble(TrialBalanceEntry::getDebit)
                    .sum();
            Double totalCredit = entries.stream()
                    .mapToDouble(TrialBalanceEntry::getCredit)
                    .sum();

            if (!totalDebit.equals(totalCredit)) {
                isValid = false;
                errorMassage = "Debit is not equal to credit";
            }
        } catch (Exception e) {
            isValid = false;
            errorMassage = "Debit is not equal to credit";
        }
        handelFile(file, isValid);
        if (!isValid) {
            throw new TrialBalanceNotValidException(errorMassage);
        }
    }

    public String saveFile(TrialBalance trialBalance) {

        TrialBalance id = repository.findByFileName(trialBalance.getFileName());
        if (id != null) {
            repository.findById(id.getId()).ifPresent(repository::delete);
        }
        TrialBalance trialBalance1 = saveFinancialReport(trialBalance);
        repository.save(trialBalance1);
        return trialBalance.getFileName();
    }

    public TrialBalance convertToObj(InputStream file, String fileName) throws IOException {
        TrialBalance trialBalance = new TrialBalance();
        List<TrialBalanceEntry> entries = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            trialBalance.setFileName(fileName);
            sheet.forEach(row1 -> {
                TrialBalanceEntry entry = new TrialBalanceEntry();
                if (row1.getRowNum() != 0) {
                    entry.setCode(String.valueOf((long) row1.getCell(0).getNumericCellValue()));
                    entry.setAccountName(row1.getCell(1).getStringCellValue());
                    entry.setDebit(row1.getCell(2).getNumericCellValue());
                    entry.setCredit(row1.getCell(3).getNumericCellValue());
                    entry.setTrialBalance(trialBalance);
                    entries.add(entry);
                }
            });
        }
        trialBalance.setTrailBalanceEntries(entries);
        return trialBalance;
    }

    public void handelFile(MultipartFile file, boolean isValid) throws IOException {

        Path tempPath = Paths.get("E:/CapstoneFiles/temp/" + file.getOriginalFilename());

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path destination;


        if (!isValid) {
            destination = Paths.get("E:/CapstoneFiles/fail/" + timestamp + "  " + file.getOriginalFilename());

            Files.createDirectories(destination.getParent());
            Files.move(tempPath, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved to: " + destination);
        }
    }


    public TrialBalance saveFinancialReport(TrialBalance trialBalance) {
        List<TrialBalanceEntry> entries = trialBalance.getTrailBalanceEntries();
        FinancialReport financialReport = new FinancialReport();
        Double assets = 0.0;
        Double liabilities = 0.0;
        Double equity = 0.0;
        Double revenue = 0.0;
        Double expenses = 0.0;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getCode().startsWith("1")) {
                assets += entries.get(i).getDebit() - entries.get(i).getCredit();
            } else if (entries.get(i).getCode().startsWith("2")) {
                liabilities += entries.get(i).getCredit()-entries.get(i).getDebit();
            } else if (entries.get(i).getCode().startsWith("3")) {
                equity += entries.get(i).getCredit()-entries.get(i).getDebit();
            } else if (entries.get(i).getCode().startsWith("4")) {
                revenue += entries.get(i).getCredit()-entries.get(i).getDebit();
            } else if (entries.get(i).getCode().startsWith("5")) {
                expenses += entries.get(i).getDebit() - entries.get(i).getCredit();
            }
        }
        financialReport.setAssets(assets);
        financialReport.setLiabilities(liabilities);
        financialReport.setEquity(equity);
        financialReport.setRevenue(revenue);
        financialReport.setExpenses(expenses);

        trialBalance.setFinancialReport(financialReport);
        financialReport.setTrialBalance(trialBalance);

        return trialBalance;

    }

}
