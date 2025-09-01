package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateParticipantRequest {
    private String fullName;
    private String contact;
}
