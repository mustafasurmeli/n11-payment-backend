package com.n11.payment.service.notification;


import org.springframework.stereotype.Component;

@Component
public class EmailNotificationHandler extends NotificationHandler{
    @Override
    protected boolean tryNotify(String message) {
        System.out.println("[EMAIL] trying to send: " + message);

        //simülasyon
        boolean success = Math.random() > 0.5;

        if(success){
            System.out.println("[EMAIL] Sent successfully");
        } else{
            System.out.println("[EMAIL] Failed, falling back to next handler");
        }
        return success;
    }
}
