package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContestParticipantRepository extends JpaRepository<ContestParticipantEntity, UUID> {

    List<ContestParticipantEntity> findByContest_Id(UUID contestId);

    Optional<ContestParticipantEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    @Query("SELECT p FROM ContestParticipantEntity p LEFT JOIN FETCH p.contest WHERE p.login = :login")
    Optional<ContestParticipantEntity> findByLoginWithContest(@Param("login") String login);

    @Query("""
    SELECT cp FROM ContestParticipantEntity cp
    JOIN FETCH cp.participant p
    WHERE cp.contest.id = :contestId
    ORDER BY cp.result DESC
""")
    List<ContestParticipantEntity> findByContestIdWithParticipantOrderByResultDesc(@Param("contestId") UUID contestId);
}
