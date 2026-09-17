package com.agentguard.security;

import java.util.List;

import com.agentguard.model.DetectionResult;

public interface SecurityDetector {

    List<DetectionResult> detect(String normalizedPrompt);
}
