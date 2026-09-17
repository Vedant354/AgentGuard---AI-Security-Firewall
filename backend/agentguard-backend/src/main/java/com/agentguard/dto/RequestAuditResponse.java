package com.agentguard.dto;

import java.time.Instant;
import java.util.List;

public record RequestAuditResponse(
        String requestId,
        String originalPrompt,
        String normalizedPrompt,
        Integer riskScore,
        String decision,
        Boolean llmContacted,
        Instant createdAt,
        List<ThreatFindingResponse> threats,
        ReviewAuditResponse review
) {
}
