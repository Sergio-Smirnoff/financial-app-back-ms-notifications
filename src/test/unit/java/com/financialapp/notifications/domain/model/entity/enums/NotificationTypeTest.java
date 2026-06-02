package com.financialapp.notifications.domain.model.entity.enums;

import com.financialapp.notifications.domain.model.notification.NotificationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTypeTest {

    @Test
    void values_containsEverySupportedType() {
        // Given the enum / When listing values / Then the known set is present
        assertThat(NotificationType.values())
                .contains(NotificationType.PAYMENT_DUE, NotificationType.LOAN_REMINDER,
                        NotificationType.INSTALLMENT_REMINDER, NotificationType.INVESTMENT_THRESHOLD,
                        NotificationType.USER_REGISTERED, NotificationType.MONTHLY_SUMMARY,
                        NotificationType.CARD_EXPIRING, NotificationType.LOW_BALANCE,
                        NotificationType.TRANSFER_SENT, NotificationType.TRANSFER_RECEIVED);
    }

    @Test
    void valueOf_roundTripsEveryConstant() {
        // Given each constant / When valueOf its name / Then it round-trips
        for (NotificationType type : NotificationType.values()) {
            assertThat(NotificationType.valueOf(type.name())).isEqualTo(type);
        }
    }
}
