package com.example.dto.Approval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalDTO {
    private int id;
    private String status;
    private String requesterName;
    private String templateName;
    private String decisionDate;
}
