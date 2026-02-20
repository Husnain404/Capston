package com.capstone.config;

import com.capstone.batch.listener.TrialBalanceJobListener;
import com.capstone.batch.writer.TrialBalanceWriter;
import com.capstone.model.TrialBalance;
import com.capstone.service.TrialBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;



    @Bean
    @StepScope
    public ItemReader<TrialBalance> itemReader(@Value("#{jobParameters['filePath']}") String filePath, TrialBalanceService trialBalanceService)  {

        return new ItemReader<TrialBalance>() {
            private boolean read = false;

            @Override
            public TrialBalance read() throws Exception {
                if (read) return null;
                read = true;

                File file = new File(filePath);
                System.out.println("DEBUG: File exists? " + file.exists());
                System.out.println("DEBUG: Absolute path: " + file.getAbsolutePath());

                try (InputStream is = new FileInputStream(file)) {
                    return trialBalanceService.convertToObj(is, file.getName());
                }
            }
        };
    }

    @Bean
    public Step step( ItemReader<TrialBalance> itemReader, TrialBalanceWriter writer){
        return new StepBuilder("cc", jobRepository)
                .<TrialBalance,TrialBalance>chunk(1,platformTransactionManager)
                .reader(itemReader)
                .writer(writer)
                .build();
    }
    @Bean
    public Job job(Step step, TrialBalanceJobListener listener)
    {
        return new JobBuilder("ImportTrialBalance",jobRepository)
                .preventRestart()
                .listener(listener)
                .start(step)
                .build();
    }

    @Bean
    public TrialBalanceWriter writer(){
        return new TrialBalanceWriter();
    }


}
