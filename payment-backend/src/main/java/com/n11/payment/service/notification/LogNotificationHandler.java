package com.n11.payment.service.notification;


import org.springframework.stereotype.Component;

@Component
public class LogNotificationHandler extends NotificationHandler{

    @Override
    protected boolean tryNotify(String message) {
        System.err.println("[LOG] " + message);
        return true;
    }
}
