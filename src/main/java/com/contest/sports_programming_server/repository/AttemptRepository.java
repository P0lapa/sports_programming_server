package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.AttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<AttemptEntity, UUID> {
    List<AttemptEntity> findAllByParticipant_IdAndTask_IdOrderByAttemptNumberDesc(UUID participantId, UUID taskId);
    Integer countByParticipant_IdAndTask_Id(UUID participantId, UUID taskId);
    List<AttemptEntity> findByParticipant_IdAndTask_Contest_Id(UUID participantId, UUID contestId);

    @Query("""
        SELECT a FROM AttemptEntity a
        JOIN FETCH a.task t
        JOIN FETCH a.participant p
        WHERE t.contest.id = :contestId
    """)
    List<AttemptEntity> findByTask_Contest_Id(@Param("contestId") UUID contestId);
}
