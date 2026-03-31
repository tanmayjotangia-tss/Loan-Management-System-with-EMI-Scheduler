package com.loanmanagementsystem.app.service;

public interface EmiService {
    Integer getTotalEmiByLoan(Long loanId);
    void markEmisPaid(Long loanId);
}
