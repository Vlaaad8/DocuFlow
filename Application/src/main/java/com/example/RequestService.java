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
import com.example.jpa.RelationRepository;
import com.example.login.Role;
import com.example.login.User;
import com.example.security.SignaturePort;
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
    private final RelationRepository relationRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalRequestMapper approvalRequestMapper;
    private final EmailPort emailPort;
    private final SignaturePort signaturePort;

    public List<ApprovalDTO> getToApproveRequestsForUser(int userId) {
        return approvalRepository.findByApprover_IdAndStatus(userId, ApprovalStatus.IN_PROGRESS).stream()
                .map(approvalMapper::toApprovalDTO)
                .toList();

    }

    @Transactional
    public void answerRequest(int requestId, int approverId, String answer) {
        Approval approval = approvalRepository.findById(requestId).orElseThrow(() ->
                new ApprovalException("Approval with id " + requestId + " not found"));
        if (approval.getApprover().getId() != approverId) {
            throw new ApprovalException("User with id " + approverId + " is not the approver of this request");
        }
        if (approval.getStatus() != ApprovalStatus.IN_PROGRESS) {
            throw new ApprovalException("Request is already answered");
        }

        ApprovalStatus status = ApprovalStatus.valueOf(answer.toUpperCase());
        approval.setStatus(status);
        approval.setDecisionDate(Timestamp.from(Instant.now()));

        approvalRepository.save(approval);

        if(status == ApprovalStatus.REJECTED) {
            ApprovalRequest approvalRequest = approval.getApprovalRequest();
            approvalRequest.setStatus(ApprovalRequestStatus.REJECTED);
            approvalRequestRepository.save(approvalRequest);
        }
        else {
            this.signaturePort.signDocument("D:\\Licenta\\DocuFlow\\storage\\security\\certificates\\user_"+approverId+".p12", "parola", approval.getApprovalRequest().getTemplate().getPath(), approval.getApprovalRequest().getTemplate().getPath());
            continueAnswerRequest(approval.getApprovalRequest());
        }

    }

    private void continueAnswerRequest(ApprovalRequest approvalRequest) {
        approvalRequest.setCurrentStep(approvalRequest.getCurrentStep() + 1);

        if (approvalRequest.getCurrentStep() == approvalRequest.getApprovalChain().getSteps().size()) {
            approvalRequest.setStatus(ApprovalRequestStatus.ACCEPTED);
            //TODO nu se genereaza documentul ok cred? am primit pe mail ceva aiurea ca si continut
            this.emailPort.sendEmail(approvalRequest.getTemplate().getPath(), approvalRequest.getTemplate().getUser().getEmail(), approvalRequest.getTemplate().getUser().getFirstName(), approvalRequest.getTemplate().getUser().getLastName());
        } else {
            Role nextApproverRole = approvalRequest.getApprovalChain().getSteps().get(approvalRequest.getCurrentStep()).getApproverRole();
            int previousApprover = approvalRequest.getSteps().get(approvalRequest.getCurrentStep() - 2).getApprover().getId();
            User nextApprover = relationRepository.findBossBySubordinate_IdAndBoss_Role(previousApprover, nextApproverRole);

            Approval nextApproval = new Approval();
            nextApproval.setApprover(nextApprover);
            nextApproval.setStatus(ApprovalStatus.IN_PROGRESS);
            nextApproval.setStepNumber(approvalRequest.getCurrentStep());
            nextApproval.setApprovalRequest(approvalRequest);
            nextApproval.setDecisionDate(Timestamp.from(Instant.now()));

            approvalRepository.save(nextApproval);
        }
        approvalRequestRepository.save(approvalRequest);

    }

    public List<ApprovalRequestDTO> getMyRequests(int userId) {
        return approvalRequestRepository.getByTemplate_User_Id(userId).stream()
                .map(approvalRequestMapper::toDTO)
                .toList();
    }
}
