package com.agentguard.service;

import org.springframework.stereotype.Service;

@Service
public class DemoAgentService implements DemoAgent {

    @Override
    public String respond(String prompt) {
        return "Demo Agent processed the allowed request.";
    }
}
