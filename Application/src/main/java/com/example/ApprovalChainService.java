package com.example;

import com.example.approval.ApprovalChain;
import com.example.approval.ApprovalStep;
import com.example.dto.Approval.ApprovalChainDTO;
import com.example.dto.ChainRequest;
import com.example.dtoMapper.ApprovalChainMapper;
import com.example.jpa.ApprovalChainRepository;
import com.example.jpa.ApprovalStepRepository;
import com.example.login.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApprovalChainService {

    private final ApprovalChainRepository approvalChainRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalChainMapper approvalChainMapper;


    public Role[] getRoles() {
        return Role.values();
    }

    //TODO exception and transactional
    public void createApprovalChain(ChainRequest request) {

        ApprovalChain approvalChain = new ApprovalChain();
        approvalChain.setName(request.name());

        ApprovalChain savedChain = approvalChainRepository.saveAndFlush(approvalChain);

        String[] roles = request.roles();

        for(int i = 0; i < roles.length; i++) {
            ApprovalStep step = new ApprovalStep();
            step.setApprovalChain(savedChain);
            step.setApproverRole(Role.valueOf(roles[i]));
            step.setStepNumber(i + 1);
            approvalStepRepository.save(step);
        }

    }

    public List<ApprovalChainDTO> getAllApprovalChains() {
        List<ApprovalChain> approvalChains = approvalChainRepository.findAll();
        return approvalChains.stream()
                .map(approvalChainMapper::toApprovalChainDTO)
                .toList();
    }


}
