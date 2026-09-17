package com.agentguard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agentguard.model.DetectionResult;
import com.agentguard.security.SecurityDetector;

@Service
public class SecurityEngine {

    private final List<SecurityDetector> detectors;

    public SecurityEngine(List<SecurityDetector> detectors) {
        this.detectors = List.copyOf(detectors);
    }

    public List<DetectionResult> analyze(String normalizedPrompt) {
        return detectors.stream()
                .flatMap(detector -> detector.detect(normalizedPrompt).stream())
                .toList();
    }
}
