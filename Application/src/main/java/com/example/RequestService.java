package com.example;

import com.example.approval.Approval;
import com.example.approval.ApprovalRequest;
import com.example.approval.ApprovalRequestStatus;
import com.example.approval.ApprovalStatus;
import com.example.dto.Approval.ApprovalDTO;
import com.example.dto.Approval.ApprovalRequestDTO;
import com.example.dtoMapper.ApprovalMapper;
import com.example.dtoMapper.ApprovalRequestMapper;
import com.example.email.EmailPort;
import com.example.exceptions.ApprovalException;
import com.example.jpa.ApprovalRepository;
import com.example.jpa.ApprovalRequestRepository;
import com.example.jpa.NotificationRepository;
import com.example.jpa.RelationRepository;
import com.example.login.Role;
import com.example.login.User;
import com.example.security.SignaturePort;
import com.example.state.RequestContext;
import com.example.state.RequestPipeline;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class RequestService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalMapper approvalMapper;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRequestMapper approvalRequestMapper;
    private final RequestPipeline requestPipeline;

    public List<ApprovalDTO> getToApproveRequestsForUser(int userId) {
        return approvalRepository.findByApprover_IdAndStatus(userId, ApprovalStatus.IN_PROGRESS).stream()
                .map(approvalMapper::toApprovalDTO)
                .toList();

    }

    @Transactional
    public void answerRequest(int requestId, int approverId, String answer) {
        RequestContext context = new RequestContext(requestId,approverId,answer);
        requestPipeline.execute(context);
    }



    public List<ApprovalRequestDTO> getMyRequests(int userId) {
        return approvalRequestRepository.getByTemplate_User_Id(userId).stream()
                .map(approvalRequestMapper::toDTO)
                .toList();
    }
}
