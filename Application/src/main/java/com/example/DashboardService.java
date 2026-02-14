package com.example;

import com.example.approval.ApprovalRequestStatus;
import com.example.dto.DashboardDTO;
import com.example.jpa.ApprovalRequestRepository;
import com.example.jpa.FilledTemplateRepository;
import com.example.jpa.TemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DashboardService {

    private final TemplateRepository templateRepository;
    private final FilledTemplateRepository filledTemplateRepository;
    private final ApprovalRequestRepository approvalRequestRepository;


    public DashboardDTO getDashboardData(int userID) {
        int totalTemplates = (int) templateRepository.count();
        int totalFilledTemplates = filledTemplateRepository.countByUserId(userID);
        int pendingApprovals = approvalRequestRepository.countByTemplate_User_IdAndStatus(userID, ApprovalRequestStatus.PENDING);
        int receivedApprovals = approvalRequestRepository.countByTemplate_User_IdAndStatus(userID, ApprovalRequestStatus.ACCEPTED);

        return new DashboardDTO(totalTemplates, totalFilledTemplates, pendingApprovals, receivedApprovals);
    }

}
