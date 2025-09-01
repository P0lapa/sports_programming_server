package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record NewContestParticipantDto
    (UUID id,
    String login,
    String password,
    UUID participantId,
    UUID contestId)
{ }
