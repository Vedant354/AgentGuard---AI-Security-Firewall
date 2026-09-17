package com.agentguard.dto;

import java.util.Map;

public record ThreatSummaryResponse(Map<String, Long> threatCounts) {
}
