package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record ContestParticipantDto
        (UUID id,
         String login,
         UUID participantId,
         UUID contestId){
}
