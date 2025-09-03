package com.contest.sports_programming_server.dto.request;

import com.contest.sports_programming_server.dto.ContestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateContestStatusRequest {
    @NotNull(message = "Contest status is required")
    private ContestStatus contestStatus;
}
