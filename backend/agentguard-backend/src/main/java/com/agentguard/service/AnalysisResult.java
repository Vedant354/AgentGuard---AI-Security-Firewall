package com.agentguard.service;

import java.util.List;

public record AnalysisResult(
        String requestId,
        String originalPrompt,
        String normalizedPrompt,
        int riskScore,
        String decision,
        List<String> threats
) {
}
