package com.loanmanagementsystem.app.service;
import com.loanmanagementsystem.app.dto.request.UpdateUserRequest;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.BorrowerMapper;
import com.loanmanagementsystem.app.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowerServiceImplementation implements BorrowerService {
    private final BorrowerRepository borrowerRepository;
    private final BorrowerMapper borrowerMapper;
    private final AuditService auditService;
    
    @Override
    public BorrowerResponse getBorrowerById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
        .orElseThrow(() -> new BadRequestException("Borrower not found with id: " + id));
        return borrowerMapper.toResponse(borrower);
    }

    @Override
    public List<BorrowerResponse> getAllBorrowers() {
        return borrowerMapper.toResponseList(borrowerRepository.findAll());
    }

    @Override
    public void updateBorrower(Long id, UpdateUserRequest request) {
        Borrower borrower = borrowerRepository.findById(id)
        .orElseThrow(()->new BadRequestException("Borrower not found with id: " + id));

        if(request.getName() != null){
            borrower.setName(request.getName());
        }
        if(request.getEmail() != null){
            borrower.setEmail(request.getEmail());
        }
        if(request.getPhoneNumber() != null){
            borrower.setPhoneNumber(request.getPhoneNumber());
        }
        if(request.getMonthlyIncome() != null){
            borrower.setMonthlyIncome(request.getMonthlyIncome());
        }
        if(request.getCurrentEmiAmount() != null){
            borrower.setCurrentEmiAmount(request.getCurrentEmiAmount());
        }
        if(request.getBankAccountNumber() != null){
            borrower.setBankAccountNumber(request.getBankAccountNumber());
        }
        if(request.getIfscCode() != null){
            borrower.setIfscCode(request.getIfscCode());
        }

        borrowerRepository.save(borrower);
        auditService.logAction(borrower.getId(), EntityType.USER,borrower.getId(), AuditAction.UPDATED,"Old Profile Details", "New Profile Details");

    }
}