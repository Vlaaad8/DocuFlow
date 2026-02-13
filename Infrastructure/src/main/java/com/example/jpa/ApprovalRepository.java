package com.example.jpa;

import com.example.approval.Approval;
import com.example.approval.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Integer> {
    List<Approval> findByApprover_IdAndStatus(int approverId, ApprovalStatus status);
}
