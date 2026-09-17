package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class SystemPromptExtractionDetector implements SecurityDetector {

    private static final Severity SEVERITY = Severity.HIGH;
    private static final double CONFIDENCE = 0.95;

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "\\b(?:reveal|show|print|display|output|reproduce|repeat|provide|disclose)\\s+(?:your\\s+)?(?:system\\s+prompt|system\\s+instructions)\\b",
                    "Attempts to reveal system instructions."
            ),
            new DetectionRule(
                    "\\b(?:reveal|show|print|display|output|reproduce|repeat|provide|disclose)\\s+(?:the\\s+)?hidden\\s+instructions\\b",
                    "Attempts to reveal hidden instructions."
            ),
            new DetectionRule(
                    "\\b(?:reveal|show|print|display|output|reproduce|repeat|provide|disclose)\\s+(?:the\\s+)?(?:confidential|internal)\\s+instructions\\b",
                    "Attempts to disclose confidential internal instructions."
            ),
            new DetectionRule(
                    "\\b(?:reproduce|output|repeat|show|print)\\s+(?:the\\s+)?instructions\\s+(?:given|provided|sent)\\s+(?:before|prior\\s+to)\\s+(?:the\\s+)?conversation\\b",
                    "Attempts to reproduce instructions given before the conversation."
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
                        ThreatType.SYSTEM_PROMPT_EXTRACTION,
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
