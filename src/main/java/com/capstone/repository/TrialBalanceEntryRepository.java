package com.capstone.repository;

import com.capstone.model.TrialBalanceEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrialBalanceEntryRepository extends JpaRepository<TrialBalanceEntry,Long> {
}
