package com.example.dto.Approval;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApprovalRequestDTO {
    private int id;
    private String templateTitle;
    private String templateType;
    private String status;



}
