package com.example.state;

import com.example.approval.Approval;
import com.example.jpa.ApprovalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class AnswerStep implements RequestStep {

    private final ApprovalRepository approvalRepository;

    @Override
    public void execute(RequestContext context, Runnable next) {
        Approval approval = context.getApproval();
        approval.setStatus(context.getAnswer());
        approval.setDecisionDate(new Timestamp(System.currentTimeMillis()));

        approvalRepository.save(approval);

        next.run();
    }
}
