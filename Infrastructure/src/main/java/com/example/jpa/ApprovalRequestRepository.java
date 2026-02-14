package com.example.jpa;

import com.example.approval.Approval;
import com.example.approval.ApprovalRequest;
import com.example.approval.ApprovalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Integer> {

    List<ApprovalRequest> getByTemplate_User_Id(int userId);

    int countByTemplate_User_IdAndStatus(int userId, ApprovalRequestStatus status);
}
