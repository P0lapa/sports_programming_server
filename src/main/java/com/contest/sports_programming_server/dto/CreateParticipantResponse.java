package com.contest.sports_programming_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateParticipantResponse {

    private UUID id;
    private UUID contestId;
    private String login;
    private String password;
}
