package com.example.dto.Approval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalTemplateDTO {
    private int id;
    private String status;
    private String decisionDate;
    private String approverName;
    private String approverRole;
}
