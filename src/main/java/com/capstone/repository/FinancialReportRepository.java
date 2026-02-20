package com.capstone.repository;

import com.capstone.model.FinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialReportRepository extends JpaRepository<FinancialReport,Long> {

    @Query("""
            SELECT f FROM FinancialReport f 
            JOIN FETCH f.trialBalance
            WHERE f.trialBalance.fileName LIKE concat('%', :search, '%')
            """)
    List<FinancialReport> findFinancialReportByYear(@Param("search") String search);

}
