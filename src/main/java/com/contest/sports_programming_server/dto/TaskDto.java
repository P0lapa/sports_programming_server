package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskDto {

    private UUID id;
    private String name;
    private Integer weight;
    private Integer order;
    private TaskStatus status;

}
