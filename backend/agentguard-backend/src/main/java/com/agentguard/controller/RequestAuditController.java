package com.agentguard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agentguard.dto.RequestAuditResponse;
import com.agentguard.service.RequestAuditService;

@RestController
@RequestMapping("/api/requests")
public class RequestAuditController {

    private final RequestAuditService requestAuditService;

    public RequestAuditController(RequestAuditService requestAuditService) {
        this.requestAuditService = requestAuditService;
    }

    @GetMapping
    public List<RequestAuditResponse> requests(
            @RequestParam(required = false) String decision,
            @RequestParam(required = false) Integer limit
    ) {
        return requestAuditService.recentRequests(decision, limit);
    }

    @GetMapping("/{requestId}")
    public RequestAuditResponse requestDetails(@PathVariable String requestId) {
        return requestAuditService.requestDetails(requestId);
    }
}
