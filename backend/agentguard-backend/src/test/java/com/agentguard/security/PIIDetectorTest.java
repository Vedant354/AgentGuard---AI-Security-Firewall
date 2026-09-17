package com.agentguard.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

class PIIDetectorTest {

    private final PIIDetector detector = new PIIDetector();

    @Test
    void detectsEmailAddressWithSafeFindingDetails() {
        String exampleEmail = "demo.user@example.test";

        List<DetectionResult> findings = detector.detect("contact " + exampleEmail + " for the demo");

        assertEquals(1, findings.size());
        DetectionResult finding = findings.get(0);
        assertEquals(ThreatType.PII, finding.threatType());
        assertEquals(Severity.MEDIUM, finding.severity());
        assertEquals(0.95, finding.confidence());
        assertEquals("Email address detected.", finding.description());
        assertFalse(finding.description().contains(exampleEmail));
    }

    @Test
    void detectsIndianMobileNumber() {
        List<DetectionResult> findings = detector.detect("test number: 9876543210");

        assertEquals("Indian mobile phone number detected.", findings.get(0).description());
    }

    @Test
    void detectsAadhaarLikeNumber() {
        List<DetectionResult> findings = detector.detect("synthetic identifier: 1234 5678 9012");

        assertEquals("Aadhaar-like identifier detected.", findings.get(0).description());
    }

    @Test
    void detectsPanLikeIdentifier() {
        List<DetectionResult> findings = detector.detect("synthetic pan: abcde1234f");

        assertEquals("PAN-like identifier detected.", findings.get(0).description());
    }

    @Test
    void detectsEachSupportedPiiTypeWithoutDuplicateFindings() {
        List<DetectionResult> findings = detector.detect(
                "demo.user@example.test 9876543210 1234 5678 9012 abcde1234f another@example.test"
        );

        assertEquals(4, findings.size());
        assertEquals(List.of(
                "Email address detected.",
                "Indian mobile phone number detected.",
                "Aadhaar-like identifier detected.",
                "PAN-like identifier detected."
        ), findings.stream().map(DetectionResult::description).toList());
    }

    @Test
    void doesNotFlagOrdinaryTextOrNumbers() {
        assertTrue(detector.detect("the order contains 42 items and costs 12345 credits").isEmpty());
    }

    @Test
    void returnsNoFindingForEmptyOrBlankInput() {
        assertTrue(detector.detect("").isEmpty());
        assertTrue(detector.detect("   ").isEmpty());
    }
}
