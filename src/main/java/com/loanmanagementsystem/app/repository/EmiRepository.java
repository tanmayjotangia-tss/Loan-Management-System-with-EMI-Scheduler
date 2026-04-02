package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmiRepository extends JpaRepository<Emi, Long> {

    @Query("""
    SELECT e FROM Emi e
    WHERE e.loan.id = :loanId
    AND e.status IN (
        com.loanmanagementsystem.app.entity.enums.EmiStatus.PENDING,
        com.loanmanagementsystem.app.entity.enums.EmiStatus.OVERDUE
    )
""")
    List<Emi> findUnpaidEmisByLoanId(@Param("loanId") Long loanId);

    List<Emi> findAllByLoanId(Long loanId);

    List<Emi> findAllByStatus(EmiStatus status);

    List<Emi> findAllByLoanIdAndStatus(Long loanId, EmiStatus status);

    List<Emi> findAllByDueDateBeforeAndStatus(LocalDate date, EmiStatus status);

    List<Emi> findAllByDueDateAndStatus(LocalDate dueDate, EmiStatus status);

    @Query("""
    SELECT e FROM Emi e
    JOIN FETCH e.loan l
    JOIN FETCH l.borrower
    WHERE e.dueDate = :date AND e.status = :status AND e.reminderSent = false
    """)
    List<Emi> findUpcomingEmis(@Param("date") LocalDate date, @Param("status") EmiStatus status);

    @Query("""
    SELECT e FROM Emi e
    JOIN FETCH e.loan l
    JOIN FETCH l.borrower
    WHERE e.dueDate < :today AND e.status IN (com.loanmanagementsystem.app.entity.enums.EmiStatus.PENDING, com.loanmanagementsystem.app.entity.enums.EmiStatus.OVERDUE)
    """)
    List<Emi> findOverdueEmis(@Param("today") LocalDate today);

    @Query("""
    SELECT e FROM Emi e
    WHERE e.loan.id = :loanId
    AND e.status IN (
        com.loanmanagementsystem.app.entity.enums.EmiStatus.PAID,
        com.loanmanagementsystem.app.entity.enums.EmiStatus.PENDING,
        com.loanmanagementsystem.app.entity.enums.EmiStatus.OVERDUE
    )
""")
    List<Emi> findAllActiveEmisByLoanId(@Param("loanId") Long loanId);

    boolean existsByLoanIdAndStatusNot(Long loanId, EmiStatus emiStatus);

    List<Emi> findByStatusAndDueDateBefore(EmiStatus status, LocalDate date);
}