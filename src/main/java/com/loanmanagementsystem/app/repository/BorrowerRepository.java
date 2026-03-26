package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

    Optional<Borrower> findByEmail(String email);

    List<Borrower> findAllByIsActiveTrue();

    boolean existsByEmail(String email);
}