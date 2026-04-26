package com.example.facade;

import com.example.approval.*;
import com.example.jpa.ApprovalRepository;
import com.example.jpa.ApprovalRequestRepository;
import com.example.jpa.RelationRepository;
import com.example.login.Role;
import com.example.login.User;
import com.example.template.FilledTemplate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;


@Component
@AllArgsConstructor
public class ApprovalInitiator {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRepository approvalRepository;
    private final RelationRepository relationRepository;

    public void initiateApproval(FilledTemplate filledTemplate, int userId) {
     ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setTemplate(filledTemplate);
        approvalRequest.setApprovalChain(filledTemplate.getTemplate().getApprovalChain());
        approvalRequest.setStatus(ApprovalRequestStatus.PENDING);
        approvalRequest.setCurrentStep(1);

        ApprovalRequest saved = approvalRequestRepository.saveAndFlush(approvalRequest);

        Role firstApproverRole = filledTemplate.getTemplate()
                .getApprovalChain().getSteps().get(1).getApproverRole();

        User firstApprover = relationRepository
                .findBossBySubordinate_IdAndBoss_Role(userId, firstApproverRole);

        Approval approval = new Approval();
        approval.setStatus(ApprovalStatus.IN_PROGRESS);
        approval.setApprovalRequest(saved);
        approval.setApprover(firstApprover);
        approval.setStepNumber(1);
        approval.setDecisionDate(Timestamp.from(Instant.now()));

        approvalRepository.save(approval);
    }
}