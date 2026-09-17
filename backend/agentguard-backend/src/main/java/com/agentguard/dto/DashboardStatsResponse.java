package com.agentguard.dto;

import java.util.List;
import java.util.Map;

public record DashboardStatsResponse(
        long totalRequests,
        long allowedRequests,
        long reviewRequests,
        long blockedRequests,
        long totalThreats,
        long pendingReviews,
        Map<String, Long> threatCounts,
        List<RequestSummaryResponse> recentRequests
) {
}
