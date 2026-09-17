package com.agentguard.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

class SecretDetectorTest {

    private final SecretDetector detector = new SecretDetector();

    @Test
    void detectsApiKeyAssignmentWithSafeFindingDetails() {
        String exampleCredential = "example-api-key-value";

        List<DetectionResult> findings = detector.detect("api_key=" + exampleCredential);

        assertEquals(1, findings.size());
        DetectionResult finding = findings.get(0);
        assertEquals(ThreatType.SECRET, finding.threatType());
        assertEquals(Severity.HIGH, finding.severity());
        assertEquals(0.90, finding.confidence());
        assertEquals("API key assignment detected.", finding.description());
        assertFalse(finding.description().contains(exampleCredential));
    }

    @ParameterizedTest
    @CsvSource({
            "api_secret=example-api-secret-value,API secret assignment detected.",
            "bearer example-token-value,Bearer or authorization token detected.",
            "secret=example-secret-value,Secret assignment detected.",
            "token=example-token-value,Token assignment detected."
    })
    void detectsSupportedSecretAssignmentsOrTokens(String prompt, String expectedDescription) {
        List<DetectionResult> findings = detector.detect(prompt);

        assertEquals(1, findings.size());
        assertEquals(expectedDescription, findings.get(0).description());
    }

    @Test
    void detectsPrivateKeyMarkerAsCritical() {
        List<DetectionResult> findings = detector.detect("-----begin private key-----");

        assertEquals(1, findings.size());
        DetectionResult finding = findings.get(0);
        assertEquals(Severity.CRITICAL, finding.severity());
        assertEquals(0.95, finding.confidence());
        assertEquals("Private key material detected.", finding.description());
    }

    @ParameterizedTest
    @CsvSource({
            "the secret should be stored safely",
            "explain what a token is",
            "what is an api key?",
            "this is ordinary text without credentials"
    })
    void doesNotFlagWordsWithoutCredentialValues(String prompt) {
        assertTrue(detector.detect(prompt).isEmpty());
    }

    @Test
    void returnsNoFindingForEmptyOrBlankInput() {
        assertTrue(detector.detect("").isEmpty());
        assertTrue(detector.detect("   ").isEmpty());
    }
}
