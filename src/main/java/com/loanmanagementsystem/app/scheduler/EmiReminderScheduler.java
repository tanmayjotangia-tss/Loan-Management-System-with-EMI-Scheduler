package com.loanmanagementsystem.app.scheduler;

import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.Emi;
import com.loanmanagementsystem.app.entity.enums.EmiStatus;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.entity.enums.PenaltyReason;
import com.loanmanagementsystem.app.repository.BorrowerRepository;
import com.loanmanagementsystem.app.repository.EmiRepository;
import com.loanmanagementsystem.app.service.CreditScoreService;
import com.loanmanagementsystem.app.service.NotificationService;
import com.loanmanagementsystem.app.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmiReminderScheduler {

    private static final int MISSED_EMI_THRESHOLD = 30;
    private final EmiRepository emiRepository;
    private final NotificationService notificationService;
    private final PenaltyService penaltyService;
    private final CreditScoreService creditScoreService;
    private final BorrowerRepository borrowerRepository;

    @Scheduled(cron = "*/30 * * * * ?")
//    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processEmiNotifications() {
        log.info("EMI Scheduler started");

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDate in3Days = today.plusDays(3);

        List<Emi> upcomingEmis = emiRepository.findUpcomingEmis(in3Days, EmiStatus.UPCOMING);

        for (Emi emi : upcomingEmis) {
            if (!emi.isReminderSent()) {
                sendReminder(emi);
                emi.setReminderSent(true);
            }
        }
        emiRepository.saveAll(upcomingEmis);

        List<Emi> todayDueEmis = emiRepository.findAllByDueDateAndStatus(today, EmiStatus.UPCOMING);

        for (Emi emi : todayDueEmis) {
            emi.setStatus(EmiStatus.PENDING);
            dueDayReminder(emi);
        }
        emiRepository.saveAll(todayDueEmis);

        List<Emi> candidateEmis = emiRepository.findByStatusAndDueDateBefore(EmiStatus.PENDING, today);

        List<Emi> updatedEmis = new ArrayList<>();

        for (Emi emi : candidateEmis) {

            LocalDate dueDate = emi.getDueDate();
            int graceDays = emi.getLoan().getGracePeriodDays() != null
                    ? emi.getLoan().getGracePeriodDays()
                    : 0;

            LocalDate overdueStartDate = dueDate.plusDays(graceDays);

            // Skip if still within grace period
            if (!today.isAfter(overdueStartDate)) {
                continue;
            }

            long daysOverdue = ChronoUnit.DAYS.between(overdueStartDate, today);

            if (!emi.isOverdueMarked()) {

                emi.setStatus(EmiStatus.OVERDUE);
                emi.setOverdueMarked(true);

                sendOverdueAlert(emi, daysOverdue);
                penaltyService.applyPenalty(emi.getId(), PenaltyReason.LATE_PAYMENT);

                Borrower borrower = emi.getLoan().getBorrower();
                int score = creditScoreService.updateOnOverdue(borrower.getCreditScore());
                borrower.setCreditScore(score);
                borrowerRepository.save(borrower);
            }

            Integer lastAlertDay = emi.getLastOverdueAlertDay();

            if (daysOverdue >= 7 &&
                    (lastAlertDay == null || daysOverdue - lastAlertDay >= 7)) {

                sendOverdueAlert(emi, daysOverdue);
                emi.setLastOverdueAlertDay((int) daysOverdue);
            }

            if (daysOverdue >= MISSED_EMI_THRESHOLD && !emi.isMissedEmiMarked()) {

                penaltyService.applyPenalty(emi.getId(), PenaltyReason.MISSED_EMI);
                sendMissedEmiAlert(emi);

                Borrower borrower = emi.getLoan().getBorrower();
                int score = creditScoreService.updateOnMissedEmi(borrower.getCreditScore());
                borrower.setCreditScore(score);
                borrowerRepository.save(borrower);

                emi.setMissedEmiMarked(true);
            }

            updatedEmis.add(emi);
        }

        emiRepository.saveAll(updatedEmis);

        log.info("EMI Scheduler completed: {} reminders, {} overdue processed",
                upcomingEmis.size(), updatedEmis.size());
    }

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

    private void dueDayReminder(Emi emi) {
        notificationService.sendNotification(
                emi.getLoan().getBorrower().getId(),
                NotificationType.REMINDER,
                "EMI Due Today",
                String.format(
                        "Your EMI of ₹%s for Loan ID %d is due today. Please ensure timely payment to avoid penalties.",
                        emi.getEmiAmount(),
                        emi.getLoan().getId()
                )
        );
    }

    private void sendOverdueAlert(Emi emi, long daysOverdue) {
        notificationService.sendNotification(
                emi.getLoan().getBorrower().getId(),
                NotificationType.OVERDUE,
                "Overdue EMI Alert",
                String.format("EMI overdue by %d days (Loan ID %d). Penalty will be applied.",
                        daysOverdue,
                        emi.getLoan().getId())
        );
    }

    private void sendMissedEmiAlert(Emi emi) {
        notificationService.sendNotification(
                emi.getLoan().getBorrower().getId(),
                NotificationType.OVERDUE,
                "Missed EMI Alert",
                String.format("Emi payment missed (Loan ID %d). Penalty will be applied.",
                        emi.getLoan().getId())
        );
    }
}