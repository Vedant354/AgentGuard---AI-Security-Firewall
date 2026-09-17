package com.agentguard.dto;

import java.time.Instant;

public record ReviewAuditResponse(
        String decision,
        String reviewer,
        String reason,
        Instant reviewedAt
) {
}
