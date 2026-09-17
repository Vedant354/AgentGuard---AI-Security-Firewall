package com.agentguard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agentguard.dto.DashboardStatsResponse;
import com.agentguard.dto.ThreatSummaryResponse;
import com.agentguard.repository.RequestRepository;
import com.agentguard.repository.ReviewRepository;
import com.agentguard.repository.ThreatRepository;

@Service
public class DashboardService {

    private static final int RECENT_REQUEST_LIMIT = 10;

    private final RequestRepository requestRepository;
    private final ThreatRepository threatRepository;
    private final ReviewRepository reviewRepository;
    private final ThreatSummaryService threatSummaryService;
    private final RequestAuditService requestAuditService;

    public DashboardService(
            RequestRepository requestRepository,
            ThreatRepository threatRepository,
            ReviewRepository reviewRepository,
            ThreatSummaryService threatSummaryService,
            RequestAuditService requestAuditService
    ) {
        this.requestRepository = requestRepository;
        this.threatRepository = threatRepository;
        this.reviewRepository = reviewRepository;
        this.threatSummaryService = threatSummaryService;
        this.requestAuditService = requestAuditService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse stats() {
        ThreatSummaryResponse threatSummary = threatSummaryService.summary();
        return new DashboardStatsResponse(
                requestRepository.count(),
                requestRepository.countByDecision("ALLOW"),
                requestRepository.countByDecision("REVIEW"),
                requestRepository.countByDecision("BLOCK"),
                threatRepository.count(),
                reviewRepository.countByDecision(ReviewService.PENDING),
                threatSummary.threatCounts(),
                requestAuditService.recentRequestSummaries(RECENT_REQUEST_LIMIT)
        );
    }
}
