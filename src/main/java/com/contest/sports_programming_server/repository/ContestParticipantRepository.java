package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.dto.NewContestParticipantDto;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContestParticipantRepository extends JpaRepository<ContestParticipantEntity, UUID> {

    boolean existsByContest_IdAndParticipant_Id(UUID contestId, UUID participantId);

    Optional<ContestParticipantEntity> findByContestIdAndParticipant_Id(UUID contestId, UUID participantId);

    List<ContestParticipantEntity> findByContest_Id(UUID contestId);

    Optional<ContestParticipantEntity> findByLogin(String login);

    boolean existsByLogin(String login);
}
