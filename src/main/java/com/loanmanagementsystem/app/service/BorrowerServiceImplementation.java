package com.loanmanagementsystem.app.service;
import com.loanmanagementsystem.app.dto.request.UpdateUserRequest;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;
import com.loanmanagementsystem.app.entity.Borrower;
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
    
    @Override
    public BorrowerResponse getBorrowerById(Long id) {
        Borrower borrower = borrowerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Borrower not found with id: " + id));
        return borrowerMapper.toResponse(borrower);
    }

    @Override
    public List<BorrowerResponse> getAllBorrowers() {
        return borrowerMapper.toResponseList(borrowerRepository.findAll());
    }

    @Override
    public void updateBorrower(Long id, UpdateUserRequest request) {
        Borrower borrower = borrowerRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("No borrower found"));

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
        if(request.getCreditScore() != null){
            borrower.setCreditScore(request.getCreditScore());
        }
        if(request.getBankAccountNumber() != null){
            borrower.setBankAccountNumber(request.getBankAccountNumber());
        }
        if(request.getIfscCode() != null){
            borrower.setIfscCode(request.getIfscCode());
        }

        borrowerRepository.save(borrower);
    }
}