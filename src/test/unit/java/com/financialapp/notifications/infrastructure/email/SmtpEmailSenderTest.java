package com.financialapp.notifications.infrastructure.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmtpEmailSenderTest {

    @Mock JavaMailSender mailSender;
    @Mock TemplateEngine templateEngine;
    @InjectMocks SmtpEmailSender sender;

    @Test
    void sendSimpleNotification_sendsSimpleMessage() {
        // Given recipient details / When sending a simple notification
        sender.sendSimpleNotification("to@x.com", "subj", "body");

        // Then a SimpleMailMessage is dispatched
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSimpleNotification_swallowsSendFailure() {
        // Given the mail server throws on send
        doThrow(new MailSendException("down")).when(mailSender).send(any(SimpleMailMessage.class));

        // When sending / Then the failure is swallowed (no exception propagates)
        sender.sendSimpleNotification("to@x.com", "subj", "body");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendTemplatedEmail_rendersTemplateAndSendsMime() {
        // Given a rendered template and a mime message
        MimeMessage mime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mime);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html/>");

        // When sending a templated email
        sender.sendTemplatedEmail("to@x.com", "subj", "welcome", Map.of("name", "Ada"));

        // Then the template is rendered under email/ and the mime message is sent
        verify(templateEngine).process(eqTemplate(), any(Context.class));
        verify(mailSender).send(mime);
    }

    @Test
    void sendTemplatedEmail_swallowsMessagingException_andDoesNotSend() {
        // Given a real MimeMessage and an invalid recipient address (helper.setTo throws AddressException)
        MimeMessage realMime = new JavaMailSenderImpl().createMimeMessage();
        when(mailSender.createMimeMessage()).thenReturn(realMime);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html/>");

        // When sending to a malformed address / Then the MessagingException is swallowed and nothing is sent
        sender.sendTemplatedEmail("not a valid address", "subj", "welcome", Map.of());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    private static String eqTemplate() {
        return org.mockito.ArgumentMatchers.eq("email/welcome");
    }
}
