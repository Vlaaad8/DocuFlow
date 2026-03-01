package com.example;

public interface NotificationPort {
    void sendToUser(int userId, Notification notification);
}