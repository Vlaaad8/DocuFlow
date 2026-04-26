package com.example.builder;

import com.example.Notification;
import com.example.login.User;

public class NotificationBuilder {
    private User recipient;
    private String title;
    private String message;


    private static final String TITLE_REJECTED = "Approval Request Rejected";
    private static final String TITLE_ACCEPTED = "Approval Request Accepted";
    private static final String TITLE_NEW_APPROVAL = "New Approval";


    public static NotificationBuilder rejected() {
        return new NotificationBuilder().withTitle(TITLE_REJECTED);
    }

    public static NotificationBuilder accepted() {
        return new NotificationBuilder().withTitle(TITLE_ACCEPTED);
    }

    public static NotificationBuilder newApproval() {
        return new NotificationBuilder().withTitle(TITLE_NEW_APPROVAL);
    }


    public NotificationBuilder withRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    public NotificationBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public NotificationBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public Notification build() {
        if (recipient == null)
            throw new IllegalStateException("Notification: recipient is required");
        if (title == null || title.isBlank())
            throw new IllegalStateException("Notification: title is required");
        if (message == null || message.isBlank())
            throw new IllegalStateException("Notification: message is required");

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        return notification;
    }
}
