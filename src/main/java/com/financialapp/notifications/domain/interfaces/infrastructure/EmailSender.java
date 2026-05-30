package com.financialapp.notifications.domain.interfaces.infrastructure;

import java.util.Map;

public interface EmailSender {
    void sendSimpleNotification(String to, String subject, String text);

    void sendTemplatedEmail(String to, String subject, String template, Map<String, Object> variables);
}
