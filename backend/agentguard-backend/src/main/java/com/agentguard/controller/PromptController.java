package com.agentguard.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agentguard.dto.AnalyzePromptRequest;
import com.agentguard.dto.AnalyzePromptResponse;
import com.agentguard.service.PromptAnalysisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {

    private final PromptAnalysisService promptAnalysisService;

    public PromptController(PromptAnalysisService promptAnalysisService) {
        this.promptAnalysisService = promptAnalysisService;
    }

    @PostMapping("/analyze")
    public AnalyzePromptResponse analyze(@Valid @RequestBody AnalyzePromptRequest request) {
        return promptAnalysisService.analyze(request.getPrompt());
    }
}
