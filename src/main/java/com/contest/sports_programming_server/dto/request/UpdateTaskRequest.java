package com.contest.sports_programming_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTaskRequest {

    @NotNull(message = "Task ID is required")
    private UUID id;

    private String name;
    private String description;

    @Min(value = 1, message = "Time limit must be positive")
    private Integer timeLimit;

    @Min(value = 1, message = "Memory limit must be positive")
    private Integer memoryLimit;

    @Min(value = 1, message = "Weight must be positive")
    private Integer weight;
}
