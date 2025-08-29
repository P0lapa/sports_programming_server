package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskCheckRequest {
    UUID taskId;
    UUID participantId;
    Language language;
    String solution;
}
