package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContestParticipantRepository extends JpaRepository<ContestParticipantEntity, UUID> {
    boolean existsByContest_IdAndParticipant_Id(UUID contestId, UUID participantId);
}
