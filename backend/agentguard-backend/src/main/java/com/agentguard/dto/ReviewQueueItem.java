package com.agentguard.dto;

public class ReviewQueueItem {

    private final String requestId;
    private final Integer riskScore;
    private final String decision;
    private final String status;
    private final Boolean llmContacted;

    public ReviewQueueItem(String requestId, Integer riskScore, String decision, String status, Boolean llmContacted) {
        this.requestId = requestId;
        this.riskScore = riskScore;
        this.decision = decision;
        this.status = status;
        this.llmContacted = llmContacted;
    }

    public String getRequestId() {
        return requestId;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getDecision() {
        return decision;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getLlmContacted() {
        return llmContacted;
    }
}
