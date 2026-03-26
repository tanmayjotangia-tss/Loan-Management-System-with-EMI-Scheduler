package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.OfficerType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_officers")
@DiscriminatorValue("LOAN_OFFICER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"reviewedApplications", "approvedLoans", "verifiedDocuments"})
public class LoanOfficer extends User {

    @Column(name = "employee_number", unique = true)
    private String employeeNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "officer_type")
    private OfficerType officerType;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @OneToMany(mappedBy = "reviewedByOfficer")
    private List<LoanApplication> reviewedApplications = new ArrayList<>();

    @OneToMany(mappedBy = "approvedByOfficer")
    private List<Loan> approvedLoans = new ArrayList<>();

    @OneToMany(mappedBy = "verifiedByOfficer")
    private List<Document> verifiedDocuments = new ArrayList<>();
}
