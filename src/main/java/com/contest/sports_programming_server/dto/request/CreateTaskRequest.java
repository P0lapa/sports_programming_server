package com.contest.sports_programming_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Time limit must be positive")
    private Integer timeLimit;

    @Min(value = 1, message = "Memory limit must be positive")
    private Integer memoryLimit;

    @Min(value = 1, message = "Weight must be positive")
    private Integer weight;

}
