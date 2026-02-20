package com.capstone.batch.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TrialBalanceJobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {

        String filePath = jobExecution.getJobParameters().getString("filePath");

        if (filePath == null) return;

        Path source = Paths.get(filePath);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        try {

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {


                Path success = Paths.get("E:/CapstoneFiles/success/"+timestamp +" "+ source.getFileName());
                Files.createDirectories(success.getParent());
                Files.move(source, success, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File moved to SUCCESS folder");

            } else {

                Path failed = Paths.get("E:/CapstoneFiles/fail/" +timestamp +" "+ source.getFileName());
                Files.createDirectories(failed.getParent());
                Files.move(source, failed, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("File moved to FAILED folder");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
