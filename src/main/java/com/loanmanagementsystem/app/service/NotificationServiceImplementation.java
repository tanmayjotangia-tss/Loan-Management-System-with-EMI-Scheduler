package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.entity.Notification;
import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.NotificationStatus;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.exception.BadRequestException;
import com.loanmanagementsystem.app.mapper.NotificationMapper;
import com.loanmanagementsystem.app.repository.NotificationRepository;
import com.loanmanagementsystem.app.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImplementation implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Override
    public void sendNotification(Long userId, NotificationType type, String subject, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .subject(subject)
                .message(message)
                .emailId(user.getEmail())
                .sentAt(LocalDateTime.now())
                .status(NotificationStatus.SENT)
                .build();

        try {
            Context context = new Context();
            context.setVariable("title", subject);
            context.setVariable("message", message);
            context.setVariable("userName", user.getName());
            
            String htmlContent = templateEngine.process("notification-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHtml
            helper.setFrom(mailFrom);
            
            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", user.getEmail());
            notification.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            log.error("Failed to send email to {}", user.getEmail(), e);
            notification.setStatus(NotificationStatus.FAILED);
        }

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found with id: " + userId);
        }

        return notificationRepository.findAllByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse getNotificationById(Long id, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found with id: " + userId);
        }

        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BadRequestException("Notification not found with id: " + id + " for user id: " + userId));

        return notificationMapper.toResponse(notification);
    }
}
