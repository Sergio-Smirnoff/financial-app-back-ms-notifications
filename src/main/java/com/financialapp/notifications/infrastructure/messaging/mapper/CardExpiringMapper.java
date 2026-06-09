package com.financialapp.notifications.infrastructure.messaging.mapper;

import com.financialapp.notifications.domain.event.CardExpiring;
import com.financialapp.notifications.infrastructure.messaging.payload.CardExpiringData;

public class CardExpiringMapper {

    public static CardExpiring toDomain(CardExpiringData data) {
        return new CardExpiring(
                data.userId(),
                data.cardNumber(),
                data.bankNumber(),
                data.expiringDate()
        );
    }
}
