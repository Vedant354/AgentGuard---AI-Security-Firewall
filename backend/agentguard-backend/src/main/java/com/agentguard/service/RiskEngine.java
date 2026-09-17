package com.agentguard.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.ThreatType;

@Service
public class RiskEngine {

    private static final int MAX_RISK_SCORE = 100;

    private static final Map<ThreatType, Integer> THREAT_SCORES = Map.of(
            ThreatType.SUSPICIOUS_INSTRUCTION, 10,
            ThreatType.PROMPT_INJECTION, 40,
            ThreatType.JAILBREAK, 30,
            ThreatType.SYSTEM_PROMPT_EXTRACTION, 30,
            ThreatType.PII, 15,
            ThreatType.SECRET, 40
    );

    public int calculateRiskScore(List<DetectionResult> findings) {
        Set<ThreatType> detectedThreatTypes = findings.stream()
                .map(DetectionResult::threatType)
                .collect(Collectors.toSet());

        int score = detectedThreatTypes.stream()
                .mapToInt(threatType -> THREAT_SCORES.getOrDefault(threatType, 0))
                .sum();

        return Math.min(score, MAX_RISK_SCORE);
    }
}
