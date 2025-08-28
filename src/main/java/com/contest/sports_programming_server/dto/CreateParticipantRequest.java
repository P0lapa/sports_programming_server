package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class CreateParticipantRequest {
    String fullName;
    String contact; // почта или тг
}
