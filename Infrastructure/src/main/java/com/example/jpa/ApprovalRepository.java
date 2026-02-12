package com.example.jpa;

import com.example.approval.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval,Integer> {
}
