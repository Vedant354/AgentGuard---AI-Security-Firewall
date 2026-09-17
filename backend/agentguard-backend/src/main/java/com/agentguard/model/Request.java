package com.agentguard.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalPrompt;

    @Column(columnDefinition = "TEXT")
    private String normalizedPrompt;

    @Column(nullable = false)
    private Integer riskScore;

    @Column(nullable = false)
    private String decision;

    @Column(nullable = false)
    private Boolean llmContacted;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Request() {
    }

    public Request(
            String requestId,
            String originalPrompt,
            String normalizedPrompt,
            Integer riskScore,
            String decision,
            Boolean llmContacted
    ) {
        this.requestId = requestId;
        this.originalPrompt = originalPrompt;
        this.normalizedPrompt = normalizedPrompt;
        this.riskScore = riskScore;
        this.decision = decision;
        this.llmContacted = llmContacted;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOriginalPrompt() {
        return originalPrompt;
    }

    public void setOriginalPrompt(String originalPrompt) {
        this.originalPrompt = originalPrompt;
    }

    public String getNormalizedPrompt() {
        return normalizedPrompt;
    }

    public void setNormalizedPrompt(String normalizedPrompt) {
        this.normalizedPrompt = normalizedPrompt;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Boolean getLlmContacted() {
        return llmContacted;
    }

    public void setLlmContacted(Boolean llmContacted) {
        this.llmContacted = llmContacted;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
