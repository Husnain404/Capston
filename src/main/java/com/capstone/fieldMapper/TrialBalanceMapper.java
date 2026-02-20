package com.capstone.fieldMapper;

import com.capstone.model.TrialBalance;
import com.capstone.model.TrialBalanceEntry;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;



public class TrialBalanceMapper implements FieldSetMapper {
    @Override
    public TrialBalance mapFieldSet(FieldSet fieldSet) throws BindException {
        TrialBalance trialBalance = new TrialBalance();
        TrialBalanceEntry entry = new TrialBalanceEntry();

        entry.setCode("Code");
        entry.setAccountName(" Account Name");

        Double debit = fieldSet.readDouble("Debit");
        Double credit = fieldSet.readDouble("Credit");

        entry.setDebit(debit);
        entry.setCredit(credit);



        return null;
    }
}
