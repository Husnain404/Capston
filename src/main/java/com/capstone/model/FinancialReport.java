package com.capstone.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class FinancialReport {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private Double assets;
    private Double liabilities;
    private Double equity;
    private Double revenue;
    private Double expenses;


    @OneToOne
    @JoinColumn(name = "trial_balance_id")
    @JsonBackReference
    private TrialBalance trialBalance;
}
