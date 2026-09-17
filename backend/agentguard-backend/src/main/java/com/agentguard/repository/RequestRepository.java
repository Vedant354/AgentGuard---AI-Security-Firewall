package com.agentguard.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agentguard.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequestId(String requestId);

    long countByDecision(String decision);

    java.util.List<Request> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

    java.util.List<Request> findAllByDecisionOrderByCreatedAtDescIdDesc(String decision, Pageable pageable);
}
