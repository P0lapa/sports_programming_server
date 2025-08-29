package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.dto.TaskListItemDto;
import com.contest.sports_programming_server.entity.ParticipantEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    @Query("""
  select new com.contest.sports_programming_server.dto.TaskListItemDto(
    t.id, t.name, t.description, c.id, c.name
  )
  from TaskEntity t
  join t.contest c
""")
    List<TaskListItemDto> findAllAsDto();

    List<TaskEntity> findByContest_Id(UUID contestId);
}