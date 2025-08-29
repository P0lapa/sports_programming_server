package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.dto.ContestParticipantDto;
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

    @Query("SELECT new com.contest.sports_programming_server.dto.ContestParticipantDto(" +
            "cp.id, cp.login, cp.password, p.id, c.id) " +
            "FROM ContestParticipantEntity cp " +
            "JOIN cp.contest c " +
            "JOIN cp.participant p " +
            "WHERE c.id = :contestId")
    List<ContestParticipantDto> findAllByContest_IdAsDto(@Param("contestId") UUID contestId);

    Optional<ContestParticipantEntity> findByLogin(String login);
    boolean existsByLogin(String login);
}
