package com.n11.payment.service.notification;

public abstract class NotificationHandler {

    protected NotificationHandler next;

    public NotificationHandler setNext(NotificationHandler next){
        this.next = next;
        return next;
    }

    public final void notify(String message){
        boolean handled = tryNotify(message);
        if (!handled && next != null){
            next.notify(message);
        }
    }

    protected abstract boolean tryNotify(String message);
}
