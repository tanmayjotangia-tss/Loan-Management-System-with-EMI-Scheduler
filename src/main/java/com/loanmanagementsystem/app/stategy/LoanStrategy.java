package com.loanmanagementsystem.app.stategy;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.Loan;

import java.util.List;

public interface LoanStrategy {
    List<Emi> generateSchedule(Loan loan);
}
