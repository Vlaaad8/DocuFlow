package com.example.dto;

public record DashboardDTO(int totalTemplates, int totalGeneratedDocuments,int pendingApprovals, int receivedApprovals) {
}