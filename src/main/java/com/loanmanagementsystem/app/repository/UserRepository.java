package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailAndIsActiveTrue(String email);
}