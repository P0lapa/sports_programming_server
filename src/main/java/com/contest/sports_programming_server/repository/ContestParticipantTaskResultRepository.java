package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ContestParticipantTaskResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContestParticipantTaskResultRepository extends JpaRepository<ContestParticipantTaskResultEntity, UUID> {

    List<ContestParticipantTaskResultEntity> findByParticipant_Id(UUID participantId);

}
