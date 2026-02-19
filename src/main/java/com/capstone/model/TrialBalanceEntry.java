package com.capstone.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TrialBalanceEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String accountName;

    private Double debit;

    private Double credit;

    @ManyToOne
    @JoinColumn(name = "trial_balance_id")
    @JsonBackReference
    private TrialBalance trialBalance;

}
