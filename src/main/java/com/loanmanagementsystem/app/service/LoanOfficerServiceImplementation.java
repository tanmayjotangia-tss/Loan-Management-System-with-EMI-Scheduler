package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.LoanOfficerResponse;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
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
    private final AuditService auditService;
    
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

        boolean oldValue=loanOfficer.getIsAvailable();
        if(oldValue==isAvailable){
            throw new BadRequestException("Availability is already set to the requested value");
        }
        loanOfficer.setIsAvailable(isAvailable);
        loanOfficerRepository.save(loanOfficer);

        auditService.logAction(id, EntityType.USER,id, AuditAction.STATUS_CHANGED, "Availability: "+String.valueOf(oldValue), "Availability: "+String.valueOf(isAvailable));

    }

    @Override
    public void updateLoanOfficerType(Long id, OfficerType officerType) {
        LoanOfficer loanOfficer = loanOfficerRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Loan Officer not found with id: " + id));

        OfficerType oldValue=loanOfficer.getOfficerType();

        if(oldValue==officerType){
            throw new BadRequestException("Officer type is already set to the requested value");
        }
        loanOfficer.setOfficerType(officerType);
        loanOfficerRepository.save(loanOfficer);

        auditService.logAction(id, EntityType.USER,id, AuditAction.UPDATED, oldValue.name(), officerType.name());

    }
}   