package com.loanmanagementsystem.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "monthly_income", precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.0", message = "Current EMI cannot be negative")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "current_emi_amount", precision = 15, scale = 2)
    private BigDecimal currentEmiAmount;

    @Column(name = "credit_score", nullable = false)
    private Integer creditScore = -1;

    @Size(max = 30, message = "Bank account number cannot exceed 30 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Bank account number must contain only digits")
    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Size(max = 11, message = "IFSC code must be 11 characters")
    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;

    @DecimalMin(value = "0.0", message = "Surplus amount cannot be negative")
    @Digits(integer = 13, fraction = 2)
    @Column(name= "surplus_amount", precision = 15, scale = 2)
    private BigDecimal surplusAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanApplication> loanApplications = new ArrayList<>();

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
}