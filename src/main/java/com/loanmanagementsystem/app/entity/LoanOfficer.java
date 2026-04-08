package com.loanmanagementsystem.app.entity;

import com.loanmanagementsystem.app.entity.enums.OfficerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Officer type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "officer_type", nullable = false)
    private OfficerType officerType;

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100, message = "Branch name must be between 2 and 100 characters")
    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @NotNull(message = "Availability status is required")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @OneToMany(mappedBy = "reviewedByOfficer")
    private List<LoanApplication> reviewedApplications = new ArrayList<>();

    @OneToMany(mappedBy = "approvedByOfficer")
    private List<Loan> approvedLoans = new ArrayList<>();

    @OneToMany(mappedBy = "verifiedByOfficer")
    private List<Document> verifiedDocuments = new ArrayList<>();
}