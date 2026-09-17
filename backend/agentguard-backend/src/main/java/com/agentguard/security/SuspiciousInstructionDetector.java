package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class SuspiciousInstructionDetector implements SecurityDetector {

    private static final Severity SEVERITY = Severity.MEDIUM;
    private static final double CONFIDENCE = 0.75;

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "\\b(?:my|the\\s+user's)\\s+instructions\\s+(?:are|should\\s+be)\\s+(?:the\\s+)?highest\\s+priority\\b",
                    "Claims that the user's instructions have highest priority."
            ),
            new DetectionRule(
                    "\\btreat\\s+(?:this|my|the\\s+user's)\\s+(?:message|instructions?)\\s+as\\s+(?:the\\s+)?highest(?:[-\\s])priority\\s+instruction\\b",
                    "Attempts to make the user's message the highest-priority instruction."
            ),
            new DetectionRule(
                    "\\bfrom\\s+now\\s+on(?:,)?\\s+(?:always\\s+)?follow\\s+my\\s+commands\\b",
                    "Attempts to establish persistent user-controlled commands."
            ),
            new DetectionRule(
                    "\\b(?:my\\s+(?:commands|instructions)|this\\s+message)\\s+(?:override|supersede|take\\s+precedence\\s+over)\\s+(?:your|the)\\s+(?:rules|guidelines|instructions)\\b",
                    "Attempts to give the user's instructions authority over existing rules."
            ),
            new DetectionRule(
                    "\\b(?:disregard|set\\s+aside|do\\s+not\\s+follow)\\s+(?:the\\s+)?(?:application(?:[-\\s]level)?|app(?:lication)?[-\\s]level|platform)\\s+(?:instructions|rules|guidelines)\\b",
                    "Attempts to disregard application-level instructions."
            )
    );

    @Override
    public List<DetectionResult> detect(String normalizedPrompt) {
        if (normalizedPrompt == null || normalizedPrompt.isBlank()) {
            return List.of();
        }

        return DETECTION_RULES.stream()
                .filter(rule -> rule.pattern().matcher(normalizedPrompt).find())
                .findFirst()
                .map(rule -> List.of(new DetectionResult(
                        ThreatType.SUSPICIOUS_INSTRUCTION,
                        SEVERITY,
                        CONFIDENCE,
                        rule.description()
                )))
                .orElseGet(List::of);
    }

    private record DetectionRule(Pattern pattern, String description) {

        private DetectionRule(String expression, String description) {
            this(Pattern.compile(expression, Pattern.CASE_INSENSITIVE), description);
        }
    }
}
