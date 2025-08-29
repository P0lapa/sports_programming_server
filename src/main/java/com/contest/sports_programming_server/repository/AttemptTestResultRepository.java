package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.AttemptTestResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttemptTestResultRepository extends JpaRepository<AttemptTestResultEntity, UUID> {
    List<AttemptTestResultEntity> findAllByAttempt_Id(UUID attemptId);
}
