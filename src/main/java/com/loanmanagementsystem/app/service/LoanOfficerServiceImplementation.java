package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.enums.OfficerType;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.LoanOfficerMapper;
import com.loanmanagementsystem.app.repository.LoanOfficerRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanOfficerServiceImplementation implements LoanOfficerService {
    
    private final LoanOfficerRepository loanOfficerRepository;
    private final LoanOfficerMapper loanOfficerMapper;
    
    @Override
    public LoanOfficerResponse getLoanOfficerById(Long id) {
        LoanOfficer loanOfficer = loanOfficerRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Loan Officer not found with id: " + id));
        return loanOfficerMapper.toResponse(loanOfficer);
    }

    @Override
    public List<LoanOfficerResponse> getAllLoanOfficers() {
        return loanOfficerMapper.toResponseList(loanOfficerRepository.findAll());
    }

    @Override
    public List<LoanOfficerResponse> getAvailableLoanOfficers() {
        return loanOfficerMapper.toResponseList(loanOfficerRepository.findAllByIsAvailableTrue());
    }

    @Override
    public void updateLoanOfficerAvailability(Long id, Boolean isAvailable) {
        LoanOfficer loanOfficer = loanOfficerRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Loan Officer not found with id: " + id));
        loanOfficer.setIsAvailable(isAvailable);
        loanOfficerRepository.save(loanOfficer);
    }

    @Override
    public void updateLoanOfficerType(Long id, OfficerType officerType) {
        LoanOfficer loanOfficer = loanOfficerRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Loan Officer not found with id: " + id));
        loanOfficer.setOfficerType(officerType);
        loanOfficerRepository.save(loanOfficer);
    }
}   