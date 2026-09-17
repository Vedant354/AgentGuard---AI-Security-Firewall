package com.agentguard.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agentguard.dto.RequestAuditResponse;
import com.agentguard.dto.RequestSummaryResponse;
import com.agentguard.dto.ReviewAuditResponse;
import com.agentguard.dto.ThreatFindingResponse;
import com.agentguard.model.Request;
import com.agentguard.model.Review;
import com.agentguard.model.Threat;
import com.agentguard.model.ThreatType;
import com.agentguard.repository.RequestRepository;
import com.agentguard.repository.ReviewRepository;
import com.agentguard.repository.ThreatRepository;

@Service
public class RequestAuditService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private static final String REDACTED_PROMPT = "[REDACTED]";
    private static final Set<String> VALID_DECISIONS = Set.of("ALLOW", "REVIEW", "BLOCK");

    private final RequestRepository requestRepository;
    private final ThreatRepository threatRepository;
    private final ReviewRepository reviewRepository;

    public RequestAuditService(
            RequestRepository requestRepository,
            ThreatRepository threatRepository,
            ReviewRepository reviewRepository
    ) {
        this.requestRepository = requestRepository;
        this.threatRepository = threatRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<RequestAuditResponse> recentRequests(String decision, Integer requestedLimit) {
        Pageable pageable = PageRequest.of(0, resolveLimit(requestedLimit));
        List<Request> requests = decision == null
                ? requestRepository.findAllByOrderByCreatedAtDescIdDesc(pageable)
                : requestRepository.findAllByDecisionOrderByCreatedAtDescIdDesc(normalizeDecision(decision), pageable);

        return requests.stream().map(this::toAuditResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RequestSummaryResponse> recentRequestSummaries(int limit) {
        return requestRepository.findAllByOrderByCreatedAtDescIdDesc(PageRequest.of(0, limit)).stream()
                .map(request -> new RequestSummaryResponse(
                        request.getRequestId(),
                        request.getRiskScore(),
                        request.getDecision(),
                        request.getLlmContacted(),
                        request.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public RequestAuditResponse requestDetails(String requestId) {
        Request request = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found."));
        return toAuditResponse(request);
    }

    private RequestAuditResponse toAuditResponse(Request request) {
        List<Threat> threats = threatRepository.findAllByRequestIdOrderByIdAsc(request.getRequestId());
        List<ThreatFindingResponse> threatResponses = threats.stream()
                .map(threat -> new ThreatFindingResponse(
                        threat.getThreatType().name(),
                        threat.getSeverity().name(),
                        threat.getConfidence(),
                        threat.getDescription(),
                        threat.getCreatedAt()
                ))
                .toList();
        boolean hasSensitiveThreat = threats.stream().anyMatch(this::isSensitiveThreat);
        ReviewAuditResponse review = reviewRepository.findByRequestId(request.getRequestId())
                .map(this::toReviewResponse)
                .orElse(null);

        return new RequestAuditResponse(
                request.getRequestId(),
                hasSensitiveThreat ? REDACTED_PROMPT : request.getOriginalPrompt(),
                hasSensitiveThreat ? REDACTED_PROMPT : request.getNormalizedPrompt(),
                request.getRiskScore(),
                request.getDecision(),
                request.getLlmContacted(),
                request.getCreatedAt(),
                threatResponses,
                review
        );
    }

    private int resolveLimit(Integer requestedLimit) {
        if (requestedLimit == null) {
            return DEFAULT_LIMIT;
        }
        if (requestedLimit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit must be at least 1.");
        }
        return Math.min(requestedLimit, MAX_LIMIT);
    }

    private String normalizeDecision(String decision) {
        String normalizedDecision = decision.toUpperCase(Locale.ROOT);
        if (!VALID_DECISIONS.contains(normalizedDecision)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be ALLOW, REVIEW, or BLOCK.");
        }
        return normalizedDecision;
    }

    private boolean isSensitiveThreat(Threat threat) {
        return threat.getThreatType() == ThreatType.PII || threat.getThreatType() == ThreatType.SECRET;
    }

    private ReviewAuditResponse toReviewResponse(Review review) {
        return new ReviewAuditResponse(
                review.getDecision(),
                review.getReviewer(),
                review.getReason(),
                review.getReviewedAt()
        );
    }
}
