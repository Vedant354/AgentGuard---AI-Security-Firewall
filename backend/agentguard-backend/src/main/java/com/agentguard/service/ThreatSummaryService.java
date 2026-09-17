package com.agentguard.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agentguard.dto.ThreatSummaryResponse;
import com.agentguard.model.Threat;
import com.agentguard.model.ThreatType;
import com.agentguard.repository.ThreatRepository;

@Service
public class ThreatSummaryService {

    private final ThreatRepository threatRepository;

    public ThreatSummaryService(ThreatRepository threatRepository) {
        this.threatRepository = threatRepository;
    }

    @Transactional(readOnly = true)
    public ThreatSummaryResponse summary() {
        Map<String, Long> counts = emptyThreatCounts();
        for (Threat threat : threatRepository.findAll()) {
            String threatType = threat.getThreatType().name();
            counts.put(threatType, counts.get(threatType) + 1);
        }
        return new ThreatSummaryResponse(counts);
    }

    private Map<String, Long> emptyThreatCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (ThreatType threatType : ThreatType.values()) {
            counts.put(threatType.name(), 0L);
        }
        return counts;
    }
}
