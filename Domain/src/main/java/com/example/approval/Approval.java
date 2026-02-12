package com.example.approval;


import com.example.login.User;
import com.example.template.Template;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "approvals")
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @ManyToOne
    private User approver;

    private Timestamp decisionDate;

    @Column(nullable = false)
    private int stepNumber;

    @ManyToOne
    private ApprovalRequest approvalRequest;

}
