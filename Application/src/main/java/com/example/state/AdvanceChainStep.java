package com.example.state;

import com.example.Notification;
import com.example.approval.Approval;
import com.example.approval.ApprovalRequest;
import com.example.approval.ApprovalRequestStatus;
import com.example.approval.ApprovalStatus;
import com.example.builder.NotificationBuilder;
import com.example.email.EmailPort;
import com.example.jpa.ApprovalRepository;
import com.example.jpa.ApprovalRequestRepository;
import com.example.jpa.NotificationRepository;
import com.example.jpa.RelationRepository;
import com.example.login.Role;
import com.example.login.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class AdvanceChainStep implements RequestStep {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final NotificationRepository notificationRepository;
    private final EmailPort emailPort;
    private final RelationRepository relationRepository;
    private final ApprovalRepository approvalRepository;


    @Override
    public void execute(RequestContext context, Runnable next) {
        if (context.getAnswer() == ApprovalStatus.REJECTED) {
            ApprovalRequest approvalRequest = context.getApproval().getApprovalRequest();
            approvalRequest.setStatus(ApprovalRequestStatus.REJECTED);
            approvalRequestRepository.save(approvalRequest);

            User user= approvalRequest.getTemplate().getUser();
            String message = "Your request for " + approvalRequest.getTemplate().getTemplate().getName() + " has been rejected.";
            Notification notification = NotificationBuilder.rejected().withRecipient(user).withMessage(message).build();
            notificationRepository.save(notification);

        } else {

            ApprovalRequest approvalRequest = context.getApproval().getApprovalRequest();
            approvalRequest.setCurrentStep(approvalRequest.getCurrentStep() + 1);

            if (approvalRequest.getSteps().size() + 1 == approvalRequest.getCurrentStep()) {
                approvalRequest.setStatus(ApprovalRequestStatus.ACCEPTED);
                approvalRequestRepository.save(approvalRequest);


                String path = approvalRequest.getTemplate().getPath();
                User user = approvalRequest.getTemplate().getUser();

                String message = "Your request for " + approvalRequest.getTemplate().getTemplate().getName() + " has been accepted.";
                Notification notification = NotificationBuilder.accepted().withRecipient(user).withMessage(message).build();

                this.emailPort.sendEmail(path, user.getEmail(),user.getFirstName(), user.getLastName());
                notificationRepository.save(notification);

            }

            else{
                Role nextApprover = approvalRequest.getApprovalChain().getSteps().get(approvalRequest.getCurrentStep()).getApproverRole();
                User nextUser = this.relationRepository.findBossBySubordinate_IdAndBoss_Role(context.getApproverId(), nextApprover);

                Approval approval = new Approval();
                approval.setApprover(nextUser);
                approval.setApprovalRequest(approvalRequest);
                approval.setStatus(ApprovalStatus.IN_PROGRESS);
                approval.setStepNumber(approvalRequest.getCurrentStep());
                approval.setDecisionDate(new Timestamp(System.currentTimeMillis()));

                approvalRepository.save(approval);
                approvalRequestRepository.save(approvalRequest);

            }

        }
        next.run();
    }
}


