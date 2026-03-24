package com.example.dtoMapper;

import com.example.approval.Approval;
import com.example.dto.Approval.ApprovalDTO;
import com.example.dto.Approval.ApprovalTemplateDTO;
import com.example.login.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApprovalMapper {

    ApprovalMapper INSTANCE = Mappers.getMapper(ApprovalMapper.class);

    @Mapping(target = "requesterName", source = "approvalRequest.template.user", qualifiedByName = "fullName")
    @Mapping(target = "templateName", source = "approvalRequest.template.template.name")
    @Mapping(target = "documentPath", source = "approvalRequest.template.path")
    ApprovalDTO toApprovalDTO(Approval approval);


    @Mapping(target = "approverName", source = "approval.approver" , qualifiedByName = "fullName")
    @Mapping(target = "approverRole", source = "approval.approver.role")
    ApprovalTemplateDTO toApprovalTemplateDTO(Approval approval);

    @Named("fullName")
    default String mapFullName(User user){
        return user.getFirstName()+ " " + user.getLastName();

    }
}
