package com.capstone.controller;

import com.capstone.exception.FileNameInvalidException;
import com.capstone.exception.TrialBalanceNotValidException;
import com.capstone.model.TrialBalance;
import com.capstone.response.ApiResponse;
import com.capstone.service.TrialBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.List;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trial-balance")
public class ReportController {

    private final TrialBalanceService service;
    private final JobLauncher jobLauncher;
    private final Job job;


    @PostMapping(value = "/import", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> importFileForJob(@RequestParam("file") MultipartFile file){
      try {
          if (file.isEmpty()) {
              return ResponseEntity.badRequest().body(new ApiResponse("File is empty", null));
          }
          Path tempPath = Paths.get("E:/CapstoneFiles/temp/" + file.getOriginalFilename());
          Files.createDirectories(tempPath.getParent());
          Files.write(tempPath, file.getBytes());

          service.fileNameFileValidate(file);
          service.accountingTotalValidate(file);

          JobParameters jobParameters = new JobParametersBuilder()
                  .addString("filePath",tempPath.toString())
                  .addLong("time",System.currentTimeMillis())
                  .toJobParameters();
          try{
              jobLauncher.run(job, jobParameters);
          }catch (JobExecutionAlreadyRunningException
                  | JobRestartException
                  | JobInstanceAlreadyCompleteException
                  | JobParametersInvalidException e
          ){
              e.printStackTrace();
          }

          return ResponseEntity.ok().body(new ApiResponse("File move to Success Folder",file.getOriginalFilename()));
      }catch (IOException|FileNameInvalidException |TrialBalanceNotValidException e){
          return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(),null));
      }
    }


    @PostMapping(value = "/upload", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ApiResponse> saveExileFile(@RequestParam("file") MultipartFile file)
    {
        try {
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            TrialBalance trialBalance = service.convertToObj(inputStream,fileName);
            service.saveFile(trialBalance);
            return ResponseEntity.ok().body(new ApiResponse("Added successfully",fileName));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ApiResponse("Failed to process file: " + e.getMessage(), null));
        }
    }

    @PostMapping("/check-name")
    public ResponseEntity<ApiResponse> validateName(@RequestParam("file") MultipartFile file){
        try{
            service.fileNameFileValidate(file);
            return ResponseEntity.ok().body(new ApiResponse("File Name Is valid ",file.getOriginalFilename()));
        } catch (IOException | DateTimeParseException | FileNameInvalidException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()+" File moved to: E:/CapstoneFiles/fail",null));
        }
    }

    @PostMapping("/check-total")
    public ResponseEntity<ApiResponse> validateTotal(@RequestParam("file") MultipartFile file){
        try{
            service.accountingTotalValidate(file);
            return ResponseEntity.ok().body(new ApiResponse("File Total Is valid ",file.getOriginalFilename()));
        } catch (IOException | TrialBalanceNotValidException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()+" File moved to: E:/CapstoneFiles/fail",null));
        }
    }

}
