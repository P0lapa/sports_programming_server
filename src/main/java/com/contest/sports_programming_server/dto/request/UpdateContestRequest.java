package com.contest.sports_programming_server.dto.request;

import com.contest.sports_programming_server.dto.ContestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateContestRequest {
    @NotNull(message = "Contest ID is required")
    private UUID id;

    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private ContestStatus contestStatus;
}