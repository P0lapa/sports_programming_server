package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

public record ContestParticipantDto
    (UUID id,
    String login,
    String password,
    UUID participantId,
    UUID contestId)
{ }
