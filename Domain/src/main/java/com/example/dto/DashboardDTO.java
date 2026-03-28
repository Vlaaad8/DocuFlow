package com.example.dto;

import com.example.ChartData;
import com.example.ChartInfo;

import java.util.List;
import java.util.Map;

public record DashboardDTO(int totalTemplates, int totalGeneratedDocuments, int pendingApprovals, int receivedApprovals ,List<ChartData>chartData,List<ChartInfo> sourceDistribution) {
}