package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ParticipantEntity;
import com.contest.sports_programming_server.entity.SolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<SolutionEntity, UUID> {
    @Query("""
      select s from SolutionEntity s
      where (:participantId is null or s.participant.id = :participantId)
        and (:taskId is null or s.task.id = :taskId)
        and (:contestId is null or s.task.contest.id = :contestId)
      """)
    List<SolutionEntity> findAllFiltered(@Param("participantId") UUID participantId,
                                         @Param("taskId") UUID taskId,
                                         @Param("contestId") UUID contestId);
}
