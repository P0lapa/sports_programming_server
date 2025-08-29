package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestRepository extends JpaRepository<TestEntity, UUID> {
    List<TestEntity> findAllByTask_Id(UUID taskId);
}
