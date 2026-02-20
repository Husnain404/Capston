package com.capstone.batch.writer;

import com.capstone.model.TrialBalance;
import com.capstone.service.TrialBalanceService;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TrialBalanceWriter implements ItemWriter<TrialBalance> {

    @Autowired
    private TrialBalanceService trialBalanceService;

    @Override
    public void write(@NonNull Chunk<? extends TrialBalance> chunk) throws Exception {

        System.out.println("Thread Name " + Thread.currentThread().getName());

        for (TrialBalance trialBalance : chunk.getItems()) {
            trialBalanceService.saveFile(trialBalance);
        }
    }
}
