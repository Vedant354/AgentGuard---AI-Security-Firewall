package com.agentguard.service;

import org.springframework.stereotype.Service;

import com.agentguard.dto.AgentChatResponse;

@Service
public class EnforcementService {

    private final DemoAgent demoAgent;
    private final PromptAnalysisService promptAnalysisService;
    private final ReviewService reviewService;

    public EnforcementService(
            DemoAgent demoAgent,
            PromptAnalysisService promptAnalysisService,
            ReviewService reviewService
    ) {
        this.demoAgent = demoAgent;
        this.promptAnalysisService = promptAnalysisService;
        this.reviewService = reviewService;
    }

    public AgentChatResponse enforce(AnalysisResult analysisResult) {
        return switch (analysisResult.decision()) {
            case "ALLOW" -> allow(analysisResult);
            case "REVIEW" -> review(analysisResult);
            case "BLOCK" -> response(
                    analysisResult,
                    false,
                    "This request has been blocked."
            );
            default -> response(
                    analysisResult,
                    false,
                    "This request has been blocked."
            );
        };
    }

    private AgentChatResponse allow(AnalysisResult analysisResult) {
        String response = demoAgent.respond(analysisResult.originalPrompt());
        promptAnalysisService.markDemoAgentContacted(analysisResult.requestId());
        return response(analysisResult, true, response);
    }

    private AgentChatResponse review(AnalysisResult analysisResult) {
        reviewService.createPendingReview(analysisResult.requestId());
        return response(
                analysisResult,
                false,
                "Human review is required before this request can be processed."
        );
    }

    private AgentChatResponse response(AnalysisResult analysisResult, boolean llmContacted, String response) {
        return new AgentChatResponse(
                analysisResult.requestId(),
                analysisResult.riskScore(),
                analysisResult.decision(),
                analysisResult.threats(),
                llmContacted,
                response
        );
    }
}
