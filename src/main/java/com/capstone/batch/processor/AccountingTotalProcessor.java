package com.capstone.batch.processor;

import com.capstone.exception.TrialBalanceNotValidException;
import com.capstone.model.TrialBalance;
import com.capstone.service.TrialBalanceService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AccountingTotalProcessor implements ItemProcessor<TrialBalance,TrialBalance> {

    private final TrialBalanceService service;

    @Override
    public @Nullable TrialBalance process(@NonNull TrialBalance item) throws Exception {
        try {
//            service.accountingTotalValidate(item);
            return item;
        } catch (RuntimeException e) {
            throw new TrialBalanceNotValidException(e.getMessage());
        }
    }
}
