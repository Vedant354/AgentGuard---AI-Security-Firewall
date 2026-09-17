package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class SecretDetector implements SecurityDetector {

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "(?<![A-Z0-9_])API[_-]?KEY\\s*[:=]\\s*(?:\"[^\"]+\"|'[^']+'|\\S+)",
                    Severity.HIGH,
                    0.90,
                    "API key assignment detected."
            ),
            new DetectionRule(
                    "(?<![A-Z0-9_])API[_-]?SECRET\\s*[:=]\\s*(?:\"[^\"]+\"|'[^']+'|\\S+)",
                    Severity.HIGH,
                    0.90,
                    "API secret assignment detected."
            ),
            new DetectionRule(
                    "\\b(?:BEARER\\s+[A-Z0-9._~+/-]{8,}|AUTHORIZATION\\s*[:=]\\s*(?:BEARER\\s+)?[A-Z0-9._~+/-]{8,})\\b",
                    Severity.HIGH,
                    0.90,
                    "Bearer or authorization token detected."
            ),
            new DetectionRule(
                    "-----BEGIN(?:\\s+[A-Z]+)?\\s+PRIVATE\\s+KEY-----",
                    Severity.CRITICAL,
                    0.95,
                    "Private key material detected."
            ),
            new DetectionRule(
                    "(?<![A-Z0-9_])SECRET\\s*[:=]\\s*(?:\"[^\"]+\"|'[^']+'|\\S+)",
                    Severity.HIGH,
                    0.90,
                    "Secret assignment detected."
            ),
            new DetectionRule(
                    "(?<![A-Z0-9_])TOKEN\\s*[:=]\\s*(?:\"[^\"]+\"|'[^']+'|\\S+)",
                    Severity.HIGH,
                    0.90,
                    "Token assignment detected."
            ),
            new DetectionRule(
                    "\\bAKIA[0-9A-Z]{16}\\b",
                    Severity.HIGH,
                    0.95,
                    "Cloud access key identifier detected."
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
                        ThreatType.SECRET,
                        rule.severity(),
                        rule.confidence(),
                        rule.description()
                ))
                .toList();
    }

    private record DetectionRule(Pattern pattern, Severity severity, double confidence, String description) {

        private DetectionRule(String expression, Severity severity, double confidence, String description) {
            this(Pattern.compile(expression, Pattern.CASE_INSENSITIVE), severity, confidence, description);
        }
    }
}
