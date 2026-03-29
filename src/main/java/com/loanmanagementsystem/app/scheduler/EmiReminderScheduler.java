package com.loanmanagementsystem.app.scheduler;

import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmiReminderScheduler {

    private final EmiRepository emiRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processEmiRemindersAndOverdueAlerts() {

        log.info("EMI Scheduler started");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate in3Days = today.plusDays(3);

        // 3 days prior
        List<Emi> upcomingEmis = emiRepository.findUpcomingEmis(in3Days, EmiStatus.PENDING);

        for (Emi emi : upcomingEmis) {
            sendReminder(emi);
            emi.setReminderSent(true);
        }

        List<Emi> overdueEmis = emiRepository.findOverdueEmis(today);

        for (Emi emi : overdueEmis) {

            long daysOverdue = ChronoUnit.DAYS.between(emi.getDueDate(), today);

            if (daysOverdue <= 0) continue;

            // Mark overdue
            if (emi.getStatus() == EmiStatus.PENDING) {
                emi.setStatus(EmiStatus.OVERDUE);
                emi.setOverdueMarked(true);
            }

            // Weekly alert (corrected)
            if (daysOverdue >= 7 && daysOverdue - emi.getLastOverdueAlertDay() >= 7) {
                sendOverdueAlert(emi, daysOverdue);
                emi.setLastOverdueAlertDay((int) daysOverdue);
            }
        }

        log.info("EMI Scheduler completed: {} reminders, {} overdue processed", upcomingEmis.size(), overdueEmis.size());}

    private void sendReminder(Emi emi) {
        notificationService.sendNotification(
                emi.getLoan().getBorrower().getId(),
                NotificationType.REMINDER,
                "Upcoming EMI Reminder",
                String.format("EMI of %s for Loan ID %d is due on %s",
                        emi.getEmiAmount(),
                        emi.getLoan().getId(),
                        emi.getDueDate())
        );
    }

    private void sendOverdueAlert(Emi emi, long daysOverdue) {
        notificationService.sendNotification(
                emi.getLoan().getBorrower().getId(),
                NotificationType.OVERDUE,
                "Overdue EMI Alert",
                String.format("EMI overdue by %d days (Loan ID %d)",
                        daysOverdue,
                        emi.getLoan().getId())
        );
    }
}