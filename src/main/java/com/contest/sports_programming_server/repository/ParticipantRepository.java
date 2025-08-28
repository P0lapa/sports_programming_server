package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
    boolean existsByLogin(String login);
}