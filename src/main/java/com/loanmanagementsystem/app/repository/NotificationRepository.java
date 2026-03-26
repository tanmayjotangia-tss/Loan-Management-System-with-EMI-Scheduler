package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.Notification;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserIdOrderBySentAtDesc(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    List<Notification> findAllByUserIdAndType(Long userId, NotificationType type);
}