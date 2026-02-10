package com.example.dto;

import com.example.approval.ApprovalStep;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalChainDTO {
    private int id;
    private String name;
    private List<ApprovalStepDTO> steps;
}
