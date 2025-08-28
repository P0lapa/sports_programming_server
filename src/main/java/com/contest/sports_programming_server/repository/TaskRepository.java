package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ParticipantEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {}