package com.example;

import com.example.approval.ApprovalChain;
import com.example.approval.ApprovalStep;
import com.example.dto.Approval.ApprovalChainDTO;
import com.example.dto.ChainRequest;
import com.example.dtoMapper.ApprovalChainMapper;
import com.example.exceptions.ApprovalChainException;
import com.example.jpa.ApprovalChainRepository;
import com.example.jpa.ApprovalStepRepository;
import com.example.login.Role;
import com.example.template.Template;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ApprovalChainService {

    private final ApprovalChainRepository approvalChainRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final ApprovalChainMapper approvalChainMapper;


    private final Map<Role, Integer> hierarchyLevels = Map.of(
            Role.CEO, 0,
            Role.Manager, 1,
            Role.HumanResources, 2,
            Role.IT, 2,
            Role.Finance, 2,
            Role.Law, 2,
            Role.Marketing, 3,
            Role.Sales, 3,
            Role.Employee, 4
    );


    public Role[] getRoles() {
        return Role.values();
    }

    @Transactional
    public void createApprovalChain(ChainRequest request) {

        validateApprovalChain(request);

        ApprovalChain approvalChain = new ApprovalChain();
        approvalChain.setName(request.name());

        ApprovalChain savedChain = approvalChainRepository.saveAndFlush(approvalChain);

        String[] roles = request.roles();

        for (int i = 0; i < roles.length; i++) {
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

    private void validateApprovalChain(ChainRequest request) {
        if (request.roles().length == 0) {
            throw new ApprovalChainException("Approval chain must have at least one step");
        }
        Map<String, Integer> roleCount = new HashMap<>();

        for (int i = 0; i < request.roles().length; i++) {
            String role = request.roles()[i];
            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
            if (roleCount.get(role) > 1) {
                throw new ApprovalChainException("Role " + role + " cannot appear more than once in the approval chain");
            }

            if (i > 0 && role.equals(request.roles()[i - 1])) {
                throw new ApprovalChainException("Role " + role + " cannot appear in consecutive steps");
            }
            try {
                if (hierarchyLevels.containsKey(Role.valueOf(role))) {
                    if (i > 0) {
                        String previousRole = request.roles()[i - 1];
                        if (hierarchyLevels.get(Role.valueOf(role)) > hierarchyLevels.get(Role.valueOf(previousRole))) {
                            throw new ApprovalChainException("Role " + role + " cannot be at a higher level than previous role " + previousRole);
                        }
                    }
                } else {
                    throw new ApprovalChainException("Invalid role: " + role);
                }
            } catch (IllegalArgumentException e) {
                throw new ApprovalChainException("Invalid role: " + role);
            }

        }


    }

}
