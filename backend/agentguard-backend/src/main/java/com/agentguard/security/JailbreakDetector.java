package com.agentguard.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

@Component
public class JailbreakDetector implements SecurityDetector {

    private static final Severity SEVERITY = Severity.HIGH;
    private static final double CONFIDENCE = 0.90;

    private static final List<DetectionRule> DETECTION_RULES = List.of(
            new DetectionRule(
                    "\\b(?:ignore|disable|bypass|remove|turn\\s+off)\\s+(?:your\\s+)?(?:safety|content|ethical)\\s+(?:restrictions|rules|policies|guidelines|filters)\\b",
                    "Attempts to disable or bypass safety restrictions."
            ),
            new DetectionRule(
                    "\\b(?:enable|enter|activate|switch\\s+to|turn\\s+on)\\s+(?:an?\\s+)?(?:unrestricted|uncensored|developer)\\s+mode\\b",
                    "Attempts to activate an unrestricted mode."
            ),
            new DetectionRule(
                    "\\b(?:act|behave|respond|roleplay)\\s+as\\s+(?:an?\\s+)?(?:unrestricted|uncensored|unfiltered)\\s+(?:ai|assistant|model|chatbot)\\b",
                    "Attempts to use a persona to bypass restrictions."
            ),
            new DetectionRule(
                    "\\b(?:pretend|assume)\\s+(?:you\\s+are|to\\s+be)\\s+(?:an?\\s+)?(?:unrestricted|uncensored|unfiltered)\\s+(?:ai|assistant|model|chatbot)\\b",
                    "Attempts to use a persona to bypass restrictions."
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
                        ThreatType.JAILBREAK,
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
