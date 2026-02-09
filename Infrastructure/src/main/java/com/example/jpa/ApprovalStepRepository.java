package com.example.jpa;

import com.example.approval.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Integer> {
}
