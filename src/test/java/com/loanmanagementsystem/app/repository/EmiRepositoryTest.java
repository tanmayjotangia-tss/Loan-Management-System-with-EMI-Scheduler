package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.*;
import com.loanmanagementsystem.app.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class EmiRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private EmiRepository emiRepository;

    private Loan loan;

    @BeforeEach
    void setUp() {
        Borrower borrower = new Borrower();
        borrower.setName("Test");
        borrower.setEmail("test@test.com");
        borrower.setPassword("secret");
        borrower.setRole(Role.BORROWER);
        borrower.setIsActive(true);
        borrower.setIsVerified(true);
        borrower.setMonthlyIncome(new BigDecimal("50000"));
        borrower.setCurrentEmiAmount(new BigDecimal("5000"));
        em.persist(borrower);

        LoanOfficer officer = new LoanOfficer();
        officer.setName("Officer");
        officer.setEmail("officer@test.com");
        officer.setPassword("secret");
        officer.setRole(Role.LOAN_OFFICER);
        officer.setIsActive(true);
        officer.setIsVerified(true);
        officer.setEmployeeNumber("EMP1");
        officer.setOfficerType(OfficerType.COMMON);
        officer.setIsAvailable(true);
        em.persist(officer);

        LoanApplication app = LoanApplication.builder()
                .loanType(LoanType.PERSONAL)
                .borrower(borrower)
                .requestedAmount(new BigDecimal("100000"))
                .requestedTenureMonths(12)
                .monthlyIncome(new BigDecimal("50000"))
                .currentEmi(new BigDecimal("5000"))
                .purpose("Test")
                .status(LoanApplicationStatus.APPROVED)
                .reviewedByOfficer(officer)
                .suggestedStrategy(StrategyType.FLAT_RATE_LOAN)
                .finalStrategy(StrategyType.FLAT_RATE_LOAN)
                .build();
        em.persist(app);

        loan = Loan.builder()
                .loanType(LoanType.PERSONAL)
                .loanApplication(app)
                .borrower(borrower)
                .approvedByOfficer(officer)
                .principalAmount(new BigDecimal("100000"))
                .interestRate(new BigDecimal("12"))
                .tenureMonths(12)
                .emiAmount(new BigDecimal("8884"))
                .strategyType(StrategyType.FLAT_RATE_LOAN)
                .totalPayableAmount(new BigDecimal("106000"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .status(LoanStatus.ACTIVE)
                .gracePeriodDays(5)
                .build();
        em.persist(loan);
    }

    private Emi emi(LocalDate date, EmiStatus status, boolean reminderSent) {
        return em.persist(
                Emi.builder()
                        .loan(loan)
                        .installmentNumber(1)
                        .dueDate(date)
                        .emiAmount(new BigDecimal("8884"))
                        .principalComponent(new BigDecimal("7000"))
                        .interestComponent(new BigDecimal("1000"))
                        .remainingBalance(new BigDecimal("90000"))
                        .status(status)
                        .reminderSent(reminderSent)
                        .build()
        );
    }


    @Test
    void findUpcomingEmis_returnsOnlyMatchingRecords() {
        LocalDate date = LocalDate.now().plusDays(3);

        emi(date, EmiStatus.PENDING, false);
        emi(date, EmiStatus.PAID, false);
        emi(date, EmiStatus.PENDING, true);
        emi(date.plusDays(1), EmiStatus.PENDING, false);

        em.flush();

        List<Emi> result = emiRepository.findUpcomingEmis(date, EmiStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isReminderSent()).isFalse();
    }

    @Test
    void findUpcomingEmis_returnsMultipleMatches() {
        LocalDate date = LocalDate.now().plusDays(3);

        emi(date, EmiStatus.PENDING, false);
        emi(date, EmiStatus.PENDING, false);

        em.flush();

        List<Emi> result = emiRepository.findUpcomingEmis(date, EmiStatus.PENDING);

        assertThat(result).hasSize(2);
    }

    @Test
    void findOverdueEmis_returnsOnlyPendingAndOverduePastRecords() {
        LocalDate today = LocalDate.now();

        emi(today.minusDays(1), EmiStatus.PENDING, false);
        emi(today.minusDays(2), EmiStatus.OVERDUE, false);
        emi(today.minusDays(3), EmiStatus.PAID, false);
        emi(today, EmiStatus.PENDING, false);
        emi(today.plusDays(1), EmiStatus.PENDING, false);

        em.flush();

        List<Emi> result = emiRepository.findOverdueEmis(today);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Emi::getStatus)
                .containsExactlyInAnyOrder(EmiStatus.PENDING, EmiStatus.OVERDUE);
    }
}