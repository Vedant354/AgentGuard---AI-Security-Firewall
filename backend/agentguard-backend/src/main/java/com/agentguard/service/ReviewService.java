package com.agentguard.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.agentguard.dto.ReviewDecisionRequest;
import com.agentguard.dto.ReviewDecisionResponse;
import com.agentguard.dto.ReviewQueueItem;
import com.agentguard.model.Request;
import com.agentguard.model.Review;
import com.agentguard.repository.RequestRepository;
import com.agentguard.repository.ReviewRepository;

@Service
public class ReviewService {

    public static final String PENDING = "PENDING";

    private final ReviewRepository reviewRepository;
    private final RequestRepository requestRepository;
    private final DemoAgent demoAgent;

    public ReviewService(
            ReviewRepository reviewRepository,
            RequestRepository requestRepository,
            DemoAgent demoAgent
    ) {
        this.reviewRepository = reviewRepository;
        this.requestRepository = requestRepository;
        this.demoAgent = demoAgent;
    }

    @Transactional
    public Review createPendingReview(String requestId) {
        return reviewRepository.findByRequestId(requestId)
                .orElseGet(() -> reviewRepository.save(new Review(requestId, PENDING)));
    }

    @Transactional(readOnly = true)
    public List<ReviewQueueItem> pendingReviews() {
        return reviewRepository.findAllByDecisionOrderByIdAsc(PENDING).stream()
                .map(review -> toQueueItem(review, findRequest(review.getRequestId())))
                .toList();
    }

    @Transactional
    public ReviewDecisionResponse decide(String requestId, ReviewDecisionRequest decisionRequest) {
        Review review = reviewRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found."));
        if (!PENDING.equals(review.getDecision())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review has already been decided.");
        }

        String decision = decisionRequest.getDecision().toUpperCase(Locale.ROOT);
        if (!"ALLOW".equals(decision) && !"BLOCK".equals(decision)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review decision must be ALLOW or BLOCK.");
        }

        Request request = findRequest(requestId);
        review.setDecision(decision);
        review.setReviewer(decisionRequest.getReviewer());
        review.setReason(decisionRequest.getReason());
        review.setReviewedAt(Instant.now());

        if ("ALLOW".equals(decision)) {
            String response = demoAgent.respond(request.getOriginalPrompt());
            request.setLlmContacted(true);
            return new ReviewDecisionResponse(requestId, request.getRiskScore(), decision, true, response);
        }

        return new ReviewDecisionResponse(
                requestId,
                request.getRiskScore(),
                decision,
                false,
                "This request has been blocked by human review."
        );
    }

    private Request findRequest(String requestId) {
        return requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found."));
    }

    private ReviewQueueItem toQueueItem(Review review, Request request) {
        return new ReviewQueueItem(
                review.getRequestId(),
                request.getRiskScore(),
                request.getDecision(),
                review.getDecision(),
                request.getLlmContacted()
        );
    }
}
