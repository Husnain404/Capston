package com.capstone.config;

import com.capstone.service.TrialBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;


import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@RequiredArgsConstructor
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final PlatformTransactionManager platformTransactionManager;
    private final JobRepository jobRepository;
    private final TrialBalanceService service;




}
