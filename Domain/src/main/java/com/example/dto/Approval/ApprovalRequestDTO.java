package com.example.dto.Approval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApprovalRequestDTO {
    private int id;
    private String templateTitle;
    private String templateType;
    private String status;
    private List<ApprovalTemplateDTO> approvals;



}
