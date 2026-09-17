package com.agentguard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agentguard.model.Threat;

public interface ThreatRepository extends JpaRepository<Threat, Long> {

    List<Threat> findAllByRequestIdOrderByIdAsc(String requestId);
}
