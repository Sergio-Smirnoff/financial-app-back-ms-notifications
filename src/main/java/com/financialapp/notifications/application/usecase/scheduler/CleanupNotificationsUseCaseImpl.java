package com.financialapp.notifications.application.usecase.scheduler;

import com.financialapp.notifications.domain.usecase.notifications.CleanupNotificationsUseCase;
import com.financialapp.notifications.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupNotificationsUseCaseImpl implements CleanupNotificationsUseCase {
    private final NotificationRepository notificationRepository;

    @Transactional
    public void execute() {
        log.info("Starting nightly notification cleanup...");
        LocalDateTime threshold = LocalDateTime.now().minusMonths(1);
        int deletedCount = notificationRepository.deleteOldNotifications(threshold);
        log.info("Deleted {} notifications older than 1 month.", deletedCount);
    }
}
