package com.capstone.repository;

import com.capstone.model.TrialBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrialBalanceRepository extends JpaRepository<TrialBalance,Long> {
    TrialBalance findByFileName(String fileName);

    void deleteTrialBalanceByFileName(String fileName);
}
