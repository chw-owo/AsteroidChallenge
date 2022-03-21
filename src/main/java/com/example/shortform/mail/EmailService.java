package com.example.shortform.mail;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    @Async
    void sendEmail(EmailMessage emailMessage);
}
