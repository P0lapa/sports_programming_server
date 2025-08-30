package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record TestDto(
        UUID id, Integer testNumber, Boolean isPublic,
        String inputData, String expectedOutput
) { }
