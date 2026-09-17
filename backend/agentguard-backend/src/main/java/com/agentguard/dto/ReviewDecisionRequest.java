package com.agentguard.dto;

import jakarta.validation.constraints.NotBlank;

public class ReviewDecisionRequest {

    @NotBlank
    private String decision;

    private String reviewer;
    private String reason;

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
