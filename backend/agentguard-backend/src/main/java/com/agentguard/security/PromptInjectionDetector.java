package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class PromptInjectionDetector implements SecurityDetector {

    private static final Severity SEVERITY = Severity.HIGH;
    private static final double CONFIDENCE = 0.95;

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "\\b(?:ignore|disregard)\\s+(?:all\\s+)?(?:the\\s+)?(?:previous|prior)\\s+instructions\\b",
                    "Attempts to disregard previous instructions."
            ),
            new DetectionRule(
                    "\\bforget\\s+(?:your|all|the|previous|prior)\\s+instructions\\b",
                    "Attempts to make the assistant forget its instructions."
            ),
            new DetectionRule(
                    "\\b(?:override|bypass)\\s+(?:all\\s+)?(?:the\\s+)?(?:previous|prior|current|existing)\\s+instructions\\b",
                    "Attempts to override existing instructions."
            ),
            new DetectionRule(
                    "\\b(?:replace|change)\\s+(?:all\\s+)?(?:the\\s+)?(?:current|existing|previous|prior)\\s+instructions\\b",
                    "Attempts to replace current instructions."
            ),
            new DetectionRule(
                    "\\bfrom\\s+now\\s+on\\s+follow\\s+my\\s+instructions(?:\\s+instead)?\\b",
                    "Attempts to establish attacker-controlled instructions."
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
                        ThreatType.PROMPT_INJECTION,
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
