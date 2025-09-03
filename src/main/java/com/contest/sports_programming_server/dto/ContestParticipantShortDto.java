package com.contest.sports_programming_server.dto;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.security.ContestParticipant;

import java.util.UUID;

public record ContestParticipantShortDto(
        int role,
        String login
) {

    public ContestParticipantShortDto(ContestParticipant principal){
        this(2, principal.getUsername());
    }

}
