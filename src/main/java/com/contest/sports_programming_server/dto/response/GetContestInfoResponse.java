package com.contest.sports_programming_server.dto.response;

import java.time.LocalDateTime;

public record GetContestInfoResponse(
        String name, String description, LocalDateTime startDateTime, LocalDateTime endDateTime
) {
}
