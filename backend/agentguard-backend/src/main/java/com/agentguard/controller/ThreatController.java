package com.agentguard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agentguard.dto.ThreatSummaryResponse;
import com.agentguard.service.ThreatSummaryService;

@RestController
@RequestMapping("/api/threats")
public class ThreatController {

    private final ThreatSummaryService threatSummaryService;

    public ThreatController(ThreatSummaryService threatSummaryService) {
        this.threatSummaryService = threatSummaryService;
    }

    @GetMapping("/summary")
    public ThreatSummaryResponse summary() {
        return threatSummaryService.summary();
    }
}
