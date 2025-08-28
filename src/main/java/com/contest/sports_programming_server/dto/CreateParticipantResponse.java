package com.contest.sports_programming_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateParticipantResponse {

    UUID id;
    String login;
    String password;
}
