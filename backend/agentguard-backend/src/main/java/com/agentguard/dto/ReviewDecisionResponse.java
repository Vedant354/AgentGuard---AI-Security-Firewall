package com.agentguard.dto;

public class ReviewDecisionResponse {

    private final String requestId;
    private final Integer riskScore;
    private final String decision;
    private final Boolean llmContacted;
    private final String response;

    public ReviewDecisionResponse(
            String requestId,
            Integer riskScore,
            String decision,
            Boolean llmContacted,
            String response
    ) {
        this.requestId = requestId;
        this.riskScore = riskScore;
        this.decision = decision;
        this.llmContacted = llmContacted;
        this.response = response;
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

    public Boolean getLlmContacted() {
        return llmContacted;
    }

    public String getResponse() {
        return response;
    }
}
