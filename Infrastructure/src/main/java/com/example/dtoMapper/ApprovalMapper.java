package com.example.dtoMapper;

import com.example.approval.Approval;
import com.example.dto.Approval.ApprovalDTO;
import com.example.dto.Approval.ApprovalTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {

    ApprovalMapper INSTANCE = Mappers.getMapper(ApprovalMapper.class);

    @Mapping(target = "requesterName", source = "approvalRequest.template.user.name")
    @Mapping(target = "templateName", source = "approvalRequest.template.name")


    @Mapping(target = "approverName", source = "approval.approver.name")
    @Mapping(target = "approverRole", source = "approval.approver.role")
    ApprovalTemplateDTO toApprovalTemplateDTO(Approval approval);
}
