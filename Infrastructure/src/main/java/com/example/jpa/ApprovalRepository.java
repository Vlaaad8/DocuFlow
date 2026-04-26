package com.example.jpa;

import com.example.approval.Approval;
import com.example.approval.ApprovalRequest;
import com.example.approval.ApprovalStatus;
import com.example.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Integer> {
    List<Approval> findByApprover_IdAndStatus(int approverId, ApprovalStatus status);

    Optional<Approval> findByApprovalRequestAndApprover(ApprovalRequest approvalRequest, User approver);
}
