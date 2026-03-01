package com.example;


import com.example.login.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User recipient;

    @Column(nullable = false)
    String title;

    @Column(nullable = false)
    String message;

    @Column(nullable = false)
    Timestamp timestamp;

    @Column(nullable = false)
    boolean read;





}
