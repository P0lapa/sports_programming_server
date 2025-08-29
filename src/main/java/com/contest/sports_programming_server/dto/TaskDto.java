package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskDto {
//задачи для отображения на главной странице после логина
    private UUID id;
    private String name;
    private Integer weight;
}
