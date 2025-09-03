package com.contest.sports_programming_server.dto;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.security.ContestParticipant;

import java.util.UUID;

public record ContestParticipantShortDto(
        String role,
        String login
) {

    /*public ContestParticipantShortDto(ContestParticipantEntity entity){
        this(entity.getId(), entity.getLogin());
    }*/

    public ContestParticipantShortDto(ContestParticipant principal){
        this("Participant", principal.getUsername());
    }

}
