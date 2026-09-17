package com.agentguard.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agentguard.dto.AnalyzePromptResponse;
import com.agentguard.model.DetectionResult;
import com.agentguard.model.Request;
import com.agentguard.model.Threat;
import com.agentguard.repository.RequestRepository;
import com.agentguard.repository.ThreatRepository;

@Service
public class PromptAnalysisService {

    private static final boolean DEFAULT_LLM_CONTACTED = false;

    private final RequestRepository requestRepository;
    private final ThreatRepository threatRepository;
    private final PromptNormalizer promptNormalizer;
    private final SecurityEngine securityEngine;
    private final RiskEngine riskEngine;
    private final DecisionEngine decisionEngine;

    public PromptAnalysisService(
            RequestRepository requestRepository,
            ThreatRepository threatRepository,
            PromptNormalizer promptNormalizer,
            SecurityEngine securityEngine,
            RiskEngine riskEngine,
            DecisionEngine decisionEngine
    ) {
        this.requestRepository = requestRepository;
        this.threatRepository = threatRepository;
        this.promptNormalizer = promptNormalizer;
        this.securityEngine = securityEngine;
        this.riskEngine = riskEngine;
        this.decisionEngine = decisionEngine;
    }

    @Transactional
    public AnalyzePromptResponse analyze(String prompt) {
        AnalysisResult analysisResult = analyzePrompt(prompt);

        return new AnalyzePromptResponse(
                analysisResult.requestId(),
                analysisResult.riskScore(),
                analysisResult.decision(),
                analysisResult.threats(),
                DEFAULT_LLM_CONTACTED
        );
    }

    @Transactional
    public AnalysisResult analyzePrompt(String prompt) {
        String requestId = nextRequestId();
        String normalizedPrompt = promptNormalizer.normalize(prompt);
        List<DetectionResult> findings = securityEngine.analyze(normalizedPrompt);
        List<DetectionResult> distinctFindings = distinctByThreatType(findings);
        int riskScore = riskEngine.calculateRiskScore(distinctFindings);
        String decision = decisionEngine.decide(riskScore);
        List<String> threats = distinctFindings.stream()
                .map(finding -> finding.threatType().name())
                .sorted()
                .toList();

        Request request = new Request(
                requestId,
                prompt,
                normalizedPrompt,
                riskScore,
                decision,
                DEFAULT_LLM_CONTACTED
        );
        requestRepository.save(request);
        threatRepository.saveAll(distinctFindings.stream()
                .map(finding -> new Threat(
                        requestId,
                        finding.threatType(),
                        finding.severity(),
                        finding.confidence(),
                        finding.description()
                ))
                .toList());

        return new AnalysisResult(
                requestId,
                prompt,
                normalizedPrompt,
                riskScore,
                decision,
                threats
        );
    }

    @Transactional
    public void markDemoAgentContacted(String requestId) {
        Request request = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown request ID: " + requestId));
        request.setLlmContacted(true);
    }

    private String nextRequestId() {
        long nextNumber = requestRepository.count() + 1;
        return "AG-%06d".formatted(nextNumber);
    }

    private List<DetectionResult> distinctByThreatType(List<DetectionResult> findings) {
        return new ArrayList<>(findings.stream()
                .collect(Collectors.toMap(
                        DetectionResult::threatType,
                        Function.identity(),
                        (first, ignored) -> first,
                        LinkedHashMap::new
                ))
                .values());
    }
}
