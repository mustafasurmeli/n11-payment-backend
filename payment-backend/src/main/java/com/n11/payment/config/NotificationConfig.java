package com.n11.payment.config;

import com.n11.payment.service.notification.EmailNotificationHandler;
import com.n11.payment.service.notification.LogNotificationHandler;
import com.n11.payment.service.notification.NotificationHandler;
import com.n11.payment.service.notification.SmsNotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NotificationConfig {

    @Bean
    @Primary
    public NotificationHandler notificationChain(
            EmailNotificationHandler email,
            SmsNotificationHandler   sms,
            LogNotificationHandler   log
    ){
        email.setNext(sms).setNext(log);
        return email;
    }
}
