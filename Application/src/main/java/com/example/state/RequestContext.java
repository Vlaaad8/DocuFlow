package com.example.state;

import com.example.approval.Approval;
import com.example.approval.ApprovalRequestStatus;
import com.example.approval.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RequestContext {
    private int requestId;
    private int approverId;
    private ApprovalStatus answer;
    private Approval approval;


    public RequestContext(int requestId, int approverId,String answer) {
        this.requestId = requestId;
        this.approverId = approverId;
        this.answer = ApprovalStatus.valueOf(answer.toUpperCase());
    }
}
