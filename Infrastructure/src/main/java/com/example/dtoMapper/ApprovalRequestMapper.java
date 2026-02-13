package com.example.dtoMapper;

import com.example.approval.ApprovalRequest;
import com.example.dto.Approval.ApprovalRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",uses = {ApprovalMapper.class})
public interface ApprovalRequestMapper {

    ApprovalRequestMapper INSTANCE = Mappers.getMapper(ApprovalRequestMapper.class);

    @Mapping(target = "templateTitle", source = "approvalRequest.template.template.name")
    @Mapping(target = "templateType", source = "approvalRequest.template.template.category")
    @Mapping(target = "approvals", source = "steps")
    ApprovalRequestDTO toDTO(ApprovalRequest approvalRequest);
}
