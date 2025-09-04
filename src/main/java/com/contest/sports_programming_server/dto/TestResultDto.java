package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record TestResultDto(
        UUID id,
        Boolean passed,
        String reason,
        Double timeSeconds,
        Long memoryBytes
) {
}
