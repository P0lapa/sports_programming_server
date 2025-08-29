package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskDetailsDto {

    private UUID id;
    private String name;
    private String description;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer weight;
}