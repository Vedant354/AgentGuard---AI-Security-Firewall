package com.agentguard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DecisionEngineTest {

    private final DecisionEngine decisionEngine = new DecisionEngine();

    @ParameterizedTest
    @MethodSource("decisionCases")
    void returnsTheExpectedDecisionForEachRiskScore(int riskScore, String expectedDecision) {
        assertEquals(expectedDecision, decisionEngine.decide(riskScore));
    }

    private static Stream<Arguments> decisionCases() {
        return Stream.of(
                Arguments.of(0, "ALLOW"),
                Arguments.of(15, "ALLOW"),
                Arguments.of(30, "ALLOW"),
                Arguments.of(31, "REVIEW"),
                Arguments.of(50, "REVIEW"),
                Arguments.of(70, "REVIEW"),
                Arguments.of(71, "BLOCK"),
                Arguments.of(85, "BLOCK"),
                Arguments.of(100, "BLOCK"),
                Arguments.of(-1, "ALLOW"),
                Arguments.of(101, "BLOCK")
        );
    }
}
