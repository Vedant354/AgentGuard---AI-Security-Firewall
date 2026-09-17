package com.agentguard.dto;

import jakarta.validation.constraints.NotBlank;

public class AnalyzePromptRequest {

    @NotBlank
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
