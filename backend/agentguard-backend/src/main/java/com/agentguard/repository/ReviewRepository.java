package com.agentguard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agentguard.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByRequestId(String requestId);

    List<Review> findAllByDecisionOrderByIdAsc(String decision);

    long countByDecision(String decision);
}
