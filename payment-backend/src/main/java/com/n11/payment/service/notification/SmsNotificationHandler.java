package com.n11.payment.service.notification;


import org.springframework.stereotype.Component;

@Component
public class SmsNotificationHandler extends NotificationHandler{
    @Override
    protected boolean tryNotify(String message) {
        System.out.println("[SMS] Trying to send: " + message);

        boolean success = Math.random() >0.5;

        if(success){
            System.out.println("[SMS] Sent successfully");
        } else{
            System.out.println("[SMS] Failed, falling back to next handler");
        }
        return success;
    }
}
