package com.example.websocket;

import com.example.Notification;
import com.example.NotificationPort;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@Service
public class WebSocketNotification  implements NotificationPort {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToUser(int userId, Notification message) {
        messagingTemplate.convertAndSendToUser(
                userId + "",
                "/queue/notifications",
                Map.of("message", message, "timestamp", LocalDateTime.now().toString())
        );
    }
}
