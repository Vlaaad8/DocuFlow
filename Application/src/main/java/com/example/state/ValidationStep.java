package com.example.state;


import com.example.approval.Approval;
import com.example.approval.ApprovalStatus;
import com.example.exceptions.ApprovalException;
import com.example.jpa.ApprovalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidationStep implements RequestStep {

    private final ApprovalRepository approvalRepository;

    @Override
    public void execute(RequestContext context, Runnable next) {
        Approval approval = approvalRepository.findById(context.getRequestId())
                .orElseThrow(() -> new ApprovalException(
                        "Approval with id " + context.getRequestId() + " not found"));

        if (approval.getApprover().getId() != context.getApproverId()) {
            throw new ApprovalException(
                    "User with id " + context.getApproverId() + " is not the approver of this request");
        }

        if (approval.getStatus() != ApprovalStatus.IN_PROGRESS) {
            throw new ApprovalException("Request is already answered");
        }

        context.setApproval(approval);

        next.run();
    }
}