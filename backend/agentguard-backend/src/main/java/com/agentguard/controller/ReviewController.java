package com.agentguard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agentguard.dto.ReviewDecisionRequest;
import com.agentguard.dto.ReviewDecisionResponse;
import com.agentguard.dto.ReviewQueueItem;
import com.agentguard.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewQueueItem> pendingReviews() {
        return reviewService.pendingReviews();
    }

    @PostMapping("/{requestId}/decision")
    public ReviewDecisionResponse decide(
            @PathVariable String requestId,
            @Valid @RequestBody ReviewDecisionRequest request
    ) {
        return reviewService.decide(requestId, request);
    }
}
