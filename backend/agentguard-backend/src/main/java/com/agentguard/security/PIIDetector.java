package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class PIIDetector implements SecurityDetector {

    private static final Severity SEVERITY = Severity.MEDIUM;

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\b",
                    0.95,
                    "Email address detected."
            ),
            new DetectionRule(
                    "(?<!\\d)(?:\\+91[\\s-]?|0)?[6-9]\\d{9}(?!\\d)",
                    0.90,
                    "Indian mobile phone number detected."
            ),
            new DetectionRule(
                    "(?<!\\d)\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}(?!\\d)",
                    0.90,
                    "Aadhaar-like identifier detected."
            ),
            new DetectionRule(
                    "(?<![A-Z0-9])[A-Z]{5}\\d{4}[A-Z](?![A-Z0-9])",
                    0.90,
                    "PAN-like identifier detected."
            )
    );

    @Override
    public List<DetectionResult> detect(String normalizedPrompt) {
        if (normalizedPrompt == null || normalizedPrompt.isBlank()) {
            return List.of();
        }

        return DETECTION_RULES.stream()
                .filter(rule -> rule.pattern().matcher(normalizedPrompt).find())
                .map(rule -> new DetectionResult(
                        ThreatType.PII,
                        SEVERITY,
                        rule.confidence(),
                        rule.description()
                ))
                .toList();
    }

    private record DetectionRule(Pattern pattern, double confidence, String description) {

        private DetectionRule(String expression, double confidence, String description) {
            this(Pattern.compile(expression, Pattern.CASE_INSENSITIVE), confidence, description);
        }
    }
}
