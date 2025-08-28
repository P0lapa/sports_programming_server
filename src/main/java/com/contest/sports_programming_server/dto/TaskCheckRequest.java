package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskCheckRequest {
    String user_number;
    UUID task_id;
    Integer language;
    String solution;
}
