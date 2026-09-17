package com.agentguard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;
import com.agentguard.security.SecurityDetector;

class SecurityEngineTest {

    @Test
    void returnsNoFindingsWhenNoDetectorsAreRegistered() {
        SecurityEngine securityEngine = new SecurityEngine(List.of());

        assertTrue(securityEngine.analyze("explain how https works").isEmpty());
    }

    @Test
    void collectsFindingsFromEachRegisteredDetector() {
        DetectionResult firstFinding = new DetectionResult(
                ThreatType.PROMPT_INJECTION,
                Severity.HIGH,
                0.90,
                "Test finding"
        );
        DetectionResult secondFinding = new DetectionResult(
                ThreatType.SECRET,
                Severity.CRITICAL,
                0.95,
                "Another test finding"
        );
        SecurityDetector firstDetector = prompt -> List.of(firstFinding);
        SecurityDetector secondDetector = prompt -> List.of(secondFinding);
        SecurityEngine securityEngine = new SecurityEngine(List.of(firstDetector, secondDetector));

        assertEquals(List.of(firstFinding, secondFinding), securityEngine.analyze("normalized prompt"));
    }
}
