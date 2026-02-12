package com.example.dtoMapper;

import com.example.approval.ApprovalStep;
import com.example.dto.Approval.ApprovalStepDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApprovalStepMapper {

    ApprovalStepMapper INSTANCE = Mappers.getMapper(ApprovalStepMapper.class);

    ApprovalStepDTO toApprovalStepDTO(ApprovalStep approvalStep);

    ApprovalStep toApprovalStep(ApprovalStepDTO approvalStepDTO);
}
