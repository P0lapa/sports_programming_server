package com.contest.sports_programming_server.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListItemDto {
    private UUID id;
    private String name;
    private String description;
    private UUID contestId;
    private String contestName;
}