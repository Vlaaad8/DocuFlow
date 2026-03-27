package com.example.jpa;

import com.example.approval.ApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalChainRepository extends JpaRepository<ApprovalChain, Integer> {

    ApprovalChain getById(int id);

}
