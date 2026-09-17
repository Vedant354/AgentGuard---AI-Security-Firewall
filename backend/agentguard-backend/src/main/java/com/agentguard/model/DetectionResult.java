package com.agentguard.model;

public record DetectionResult(
        ThreatType threatType,
        Severity severity,
        double confidence,
        String description
) {
}
