package com.example.dtoMapper;

import com.example.approval.ApprovalChain;
import com.example.dto.Approval.ApprovalChainDTO;
import com.example.dto.Approval.ApprovalChainOptionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ApprovalStepMapper.class})
public interface ApprovalChainMapper {
    ApprovalChainMapper INSTANCE = Mappers.getMapper(ApprovalChainMapper.class);

    ApprovalChainDTO toApprovalChainDTO(ApprovalChain approvalChain);

    ApprovalChain toApprovalChain(ApprovalChainDTO approvalChainDTO);

    ApprovalChainOptionDTO toApprovalChainOption(ApprovalChain approvalChain);
}
