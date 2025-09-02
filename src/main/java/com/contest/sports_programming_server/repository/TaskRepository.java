package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByContest_Id(UUID contestId);

    boolean existsByContest_Id(UUID id);

    @Query("""
    SELECT new com.contest.sports_programming_server.dto.TaskDto(
        t.id,
        t.name,
        t.weight,
        0,
        CASE
            WHEN COUNT(a) = 0 THEN com.contest.sports_programming_server.dto.TaskStatus.NOT_STARTED
            WHEN SUM(CASE WHEN a.success = true THEN 1 ELSE 0 END) > 0 THEN com.contest.sports_programming_server.dto.TaskStatus.COMPLETED
            ELSE com.contest.sports_programming_server.dto.TaskStatus.FAILED
        END
    )
    FROM TaskEntity t
    LEFT JOIN AttemptEntity a ON a.task.id = t.id AND a.participant.id = :participantId
    WHERE t.contest.id = :contestId
    GROUP BY t.id, t.name, t.weight
    ORDER BY t.weight
""")
    List<TaskDto> findTaskDtosForParticipant(
            @Param("contestId") UUID contestId,
            @Param("participantId") UUID participantId
    );
}