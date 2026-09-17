package com.agentguard.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agentguard.dto.AgentChatResponse;
import com.agentguard.dto.AnalyzePromptRequest;
import com.agentguard.service.AgentChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent")
public class AgentChatController {

    private final AgentChatService agentChatService;

    public AgentChatController(AgentChatService agentChatService) {
        this.agentChatService = agentChatService;
    }

    @PostMapping("/chat")
    public AgentChatResponse chat(@Valid @RequestBody AnalyzePromptRequest request) {
        return agentChatService.chat(request.getPrompt());
    }
}
