package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record TaskListItemDto(
        UUID id, String name, String description,
        UUID contestId, String contestName
) {}
