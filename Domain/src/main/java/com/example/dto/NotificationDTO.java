package com.example.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationDTO {
    private int id;
    private String message;
    private String type;
   private int recipientId;
}
