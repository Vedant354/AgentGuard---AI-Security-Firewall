package com.agentguard.dto;

import java.util.List;

public class AgentChatResponse {

    private final String requestId;
    private final Integer riskScore;
    private final String decision;
    private final List<String> threats;
    private final Boolean llmContacted;
    private final String response;

    public AgentChatResponse(
            String requestId,
            Integer riskScore,
            String decision,
            List<String> threats,
            Boolean llmContacted,
            String response
    ) {
        this.requestId = requestId;
        this.riskScore = riskScore;
        this.decision = decision;
        this.threats = threats;
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

    public List<String> getThreats() {
        return threats;
    }

    public Boolean getLlmContacted() {
        return llmContacted;
    }

    public String getResponse() {
        return response;
    }
}
