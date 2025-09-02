package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContestRepository extends JpaRepository<ContestEntity, UUID> {

    @Query("SELECT c FROM ContestEntity c LEFT JOIN FETCH c.tasks")
    List<ContestEntity> findAllWithTasks();

    @Query("SELECT c FROM ContestEntity c LEFT JOIN FETCH c.tasks WHERE c.id = :id")
    Optional<ContestEntity> findByIdWithTasks(@Param("id") UUID id);

}