package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTaskRequest {

    private String name;
    private String description;
    private UUID contestId;
}
