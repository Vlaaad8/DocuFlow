package com.example.template;


import com.example.login.User;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table( uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "field_id"}))
public class UserFieldValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String value;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserFieldValue(String value, Field field, User user) {
        this.value = value;
        this.field = field;
        this.user = user;
    }
}
