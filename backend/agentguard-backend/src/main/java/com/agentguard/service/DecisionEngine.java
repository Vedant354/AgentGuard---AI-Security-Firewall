package com.agentguard.service;

import org.springframework.stereotype.Service;

@Service
public class DecisionEngine {

    private static final int MAX_ALLOW_SCORE = 30;
    private static final int MAX_REVIEW_SCORE = 70;
    private static final int MAX_RISK_SCORE = 100;

    public String decide(int riskScore) {
        int policyScore = Math.max(0, Math.min(riskScore, MAX_RISK_SCORE));

        if (policyScore <= MAX_ALLOW_SCORE) {
            return "ALLOW";
        }
        if (policyScore <= MAX_REVIEW_SCORE) {
            return "REVIEW";
        }
        return "BLOCK";
    }
}
