package com.agentguard.dto;

import java.time.Instant;

public record RequestSummaryResponse(
        String requestId,
        Integer riskScore,
        String decision,
        Boolean llmContacted,
        Instant createdAt
) {
}
