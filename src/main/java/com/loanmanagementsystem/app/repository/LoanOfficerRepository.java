package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.enums.OfficerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanOfficerRepository extends JpaRepository<LoanOfficer, Long> {

    List<LoanOfficer> findAllByIsAvailableTrue();

    List<LoanOfficer> findAllByOfficerType(OfficerType officerType);

    List<LoanOfficer> findAllByIsAvailableTrueAndOfficerType(OfficerType officerType);

    Optional<LoanOfficer> findByEmployeeNumber(String employeeNumber);
}