package com.example;

import com.example.approval.ApprovalRequest;
import com.example.approval.ApprovalRequestStatus;
import com.example.dto.DashboardDTO;
import com.example.jpa.*;
import com.example.template.SourceOfData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class DashboardService {

    private final TemplateRepository templateRepository;
    private final FilledTemplateRepository filledTemplateRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final NotificationPort notificationPort;
    private final NotificationRepository notificationRepository;
    private final UserFieldValueRepository userFieldValueRepository;


    public DashboardDTO getDashboardData(int userID) {
        int totalTemplates = (int) templateRepository.count();
        int totalFilledTemplates = filledTemplateRepository.countByUserId(userID);
        int pendingApprovals = approvalRequestRepository.countByTemplate_User_IdAndStatus(userID, ApprovalRequestStatus.PENDING);
        int receivedApprovals = approvalRequestRepository.countByTemplate_User_IdAndStatus(userID, ApprovalRequestStatus.ACCEPTED);
        receiveNotification(userID);
        List<ChartData> chartData = getApprovalRequestsForUser(userID);
        List<ChartInfo> sourceDistribution = getSourceDistribution(userID);
        return new DashboardDTO(totalTemplates, totalFilledTemplates, pendingApprovals, receivedApprovals, chartData,sourceDistribution);
    }


    private void receiveNotification(int userId) {
        this.notificationRepository.findAll().forEach(
                notification -> notificationPort.sendToUser(userId, notification)
        );
    }

    private List<ChartData> getApprovalRequestsForUser(int userId) {
        return approvalRequestRepository.getByTemplate_User_Id(userId).stream()

                .sorted(Comparator.comparing((ApprovalRequest request) -> {
                    if (request.getSteps() != null && !request.getSteps().isEmpty()) {
                        return request.getSteps().getLast().getDecisionDate();
                    }
                    return new Timestamp(0);
                }).reversed())
                .map(request -> {

                    Timestamp timestamp = new Timestamp(0);
                    if (request.getSteps() != null && !request.getSteps().isEmpty()) {
                        timestamp = request.getSteps().getLast().getDecisionDate();
                    }

                    String title = request.getTemplate().getTemplate().getName() + " #" + request.getId();
                    String status = request.getStatus().toString();
                    return new ChartData(title, timestamp, status);
                })
                .toList();
    }

    public List<ChartInfo> getSourceDistribution(int userId) {
        List<ChartInfo> chartInfo = new ArrayList<>();
        SourceOfData[] sources = SourceOfData.values();

        for (SourceOfData source : sources) {
            int count = userFieldValueRepository.countByUser_IdAndSourceOfData(userId, source);
            chartInfo.add(new ChartInfo(source.toString(), count));
        }
        return chartInfo;
    }

}






