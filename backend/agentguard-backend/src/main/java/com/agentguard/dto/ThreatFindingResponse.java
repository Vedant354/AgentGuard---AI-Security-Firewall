package com.agentguard.dto;

import java.time.Instant;

public record ThreatFindingResponse(
        String threatType,
        String severity,
        Double confidence,
        String description,
        Instant createdAt
) {
}
