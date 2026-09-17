package com.agentguard.service;

import org.springframework.stereotype.Service;

import com.agentguard.dto.AgentChatResponse;

@Service
public class AgentChatService {

    private final PromptAnalysisService promptAnalysisService;
    private final EnforcementService enforcementService;

    public AgentChatService(PromptAnalysisService promptAnalysisService, EnforcementService enforcementService) {
        this.promptAnalysisService = promptAnalysisService;
        this.enforcementService = enforcementService;
    }

    public AgentChatResponse chat(String prompt) {
        AnalysisResult analysisResult = promptAnalysisService.analyzePrompt(prompt);
        return enforcementService.enforce(analysisResult);
    }
}
