//package com.example;
//
//
//
//import com.example.dto.NotificationDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationService {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public void sendNotificationToUser(String username, NotificationDTO notification) {
//        messagingTemplate.convertAndSendToUser(
//                username,
//                "/queue/notifications",
//                notification
//        );
//    }
//}
