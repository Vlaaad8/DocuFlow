package com.example.approval;


import com.example.login.User;
import com.example.template.Template;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table (name = "approval_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Template template;

    @ManyToOne
    private User requester;

    @ManyToOne
    private ApprovalChain approvalChain;

    @Enumerated(EnumType.STRING)
    private ApprovalRequestStatus status;

    int currentStep;

    @OneToMany(mappedBy = "approvalRequest", cascade = CascadeType.ALL)
    List<Approval> steps;



}
