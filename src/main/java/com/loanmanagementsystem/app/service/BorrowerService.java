package com.loanmanagementsystem.app.service;
import java.util.List;
import com.loanmanagementsystem.app.dto.request.UpdateUserRequest;
import com.loanmanagementsystem.app.dto.response.BorrowerResponse;

public interface BorrowerService {
    BorrowerResponse getBorrowerById(Long id);
    List<BorrowerResponse> getAllBorrowers();
    void updateBorrower(Long id, UpdateUserRequest request);
}