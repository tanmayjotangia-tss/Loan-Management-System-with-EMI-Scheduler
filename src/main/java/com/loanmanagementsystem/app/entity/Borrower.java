package com.loanmanagementsystem.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrowers")
@DiscriminatorValue("BORROWER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"loanApplications", "loans"})
public class Borrower extends User {

    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "current_emi_amount", precision = 15, scale = 2)
    private BigDecimal currentEmiAmount;

    @Column(name = "credit_score",nullable = false)
    private Integer creditScore=-1;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name= "surplus_amount", precision = 15, scale = 2)
    private BigDecimal surplusAmount= BigDecimal.ZERO;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanApplication> loanApplications = new ArrayList<>();

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
}
