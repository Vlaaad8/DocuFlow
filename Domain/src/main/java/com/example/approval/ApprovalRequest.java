package com.example.approval;


import com.example.login.User;
import com.example.template.FilledTemplate;
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

    @OneToOne
    private FilledTemplate template;

    @ManyToOne
    private ApprovalChain approvalChain;

    @Enumerated(EnumType.STRING)
    private ApprovalRequestStatus status;

    @Column(nullable = false)
    int currentStep;

    @OneToMany(mappedBy = "approvalRequest", cascade = CascadeType.ALL)
    @OrderBy("stepNumber ASC")
    private List<Approval> steps;







}
