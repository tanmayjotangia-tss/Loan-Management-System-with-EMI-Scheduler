package com.loanmanagementsystem.app.service;

import java.util.List;

import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.enums.OfficerType;

public interface LoanOfficerService {
    LoanOfficerResponse getLoanOfficerById(Long id);
    List<LoanOfficerResponse> getAllLoanOfficers();
    List<LoanOfficerResponse> getAvailableLoanOfficers();
    void updateLoanOfficerAvailability(Long id, Boolean isAvailable);
    void updateLoanOfficerType(Long id, OfficerType officerType);    
}