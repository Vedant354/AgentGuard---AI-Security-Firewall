package com.agentguard.dto;

import java.util.List;

public class AnalyzePromptResponse {

    private String requestId;
    private Integer riskScore;
    private String decision;
    private List<String> threats;
    private Boolean llmContacted;

    public AnalyzePromptResponse(
            String requestId,
            Integer riskScore,
            String decision,
            List<String> threats,
            Boolean llmContacted
    ) {
        this.requestId = requestId;
        this.riskScore = riskScore;
        this.decision = decision;
        this.threats = threats;
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

    public List<String> getThreats() {
        return threats;
    }

    public Boolean getLlmContacted() {
        return llmContacted;
    }
}
