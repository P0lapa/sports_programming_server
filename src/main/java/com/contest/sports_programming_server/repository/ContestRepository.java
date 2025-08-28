package com.contest.sports_programming_server.repository;

import com.contest.sports_programming_server.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContestRepository extends JpaRepository<ContestEntity, UUID> {}