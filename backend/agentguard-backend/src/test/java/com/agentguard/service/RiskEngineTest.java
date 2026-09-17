package com.agentguard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.agentguard.model.DetectionResult;
import com.agentguard.model.Severity;
import com.agentguard.model.ThreatType;

class RiskEngineTest {

    private final RiskEngine riskEngine = new RiskEngine();

    @Test
    void returnsZeroForNoFindings() {
        assertEquals(0, riskEngine.calculateRiskScore(List.of()));
    }

    @ParameterizedTest
    @MethodSource("individualThreatScores")
    void appliesTheConfiguredScoreForEachThreatType(ThreatType threatType, int expectedScore) {
        assertEquals(expectedScore, riskEngine.calculateRiskScore(List.of(finding(threatType))));
    }

    @Test
    void sumsScoresForMultipleThreatTypes() {
        assertEquals(70, riskEngine.calculateRiskScore(List.of(
                finding(ThreatType.PROMPT_INJECTION),
                finding(ThreatType.JAILBREAK)
        )));
    }

    @Test
    void capsScoresAtOneHundred() {
        assertEquals(100, riskEngine.calculateRiskScore(List.of(
                finding(ThreatType.SUSPICIOUS_INSTRUCTION),
                finding(ThreatType.PROMPT_INJECTION),
                finding(ThreatType.JAILBREAK),
                finding(ThreatType.SYSTEM_PROMPT_EXTRACTION),
                finding(ThreatType.PII),
                finding(ThreatType.SECRET)
        )));
    }

    @Test
    void doesNotDoubleCountDuplicateThreatTypes() {
        assertEquals(40, riskEngine.calculateRiskScore(List.of(
                finding(ThreatType.PROMPT_INJECTION),
                finding(ThreatType.PROMPT_INJECTION)
        )));
    }

    @Test
    void calculatesMixedFindingsUsingThreatTypeScoresOnly() {
        assertEquals(55, riskEngine.calculateRiskScore(List.of(
                finding(ThreatType.SUSPICIOUS_INSTRUCTION),
                finding(ThreatType.SYSTEM_PROMPT_EXTRACTION),
                finding(ThreatType.PII)
        )));
    }

    private static Stream<Arguments> individualThreatScores() {
        return Stream.of(
                Arguments.of(ThreatType.SUSPICIOUS_INSTRUCTION, 10),
                Arguments.of(ThreatType.PROMPT_INJECTION, 40),
                Arguments.of(ThreatType.JAILBREAK, 30),
                Arguments.of(ThreatType.SYSTEM_PROMPT_EXTRACTION, 30),
                Arguments.of(ThreatType.PII, 15),
                Arguments.of(ThreatType.SECRET, 40)
        );
    }

    private static DetectionResult finding(ThreatType threatType) {
        return new DetectionResult(threatType, Severity.LOW, 0.0, "Test finding");
    }
}
