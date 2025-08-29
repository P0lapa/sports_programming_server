package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.AttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttemptRepository extends JpaRepository<AttemptEntity, UUID> {
    List<AttemptEntity> findAllByParticipant_IdAndTask_Id(UUID participantId, UUID taskId);
    Integer countByParticipant_IdAndTask_Id(UUID participantId, UUID taskId);
}
