package com.contest.sports_programming_server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TaskWithAttemptsDto {
    private TaskDetailsDto task;
    private List<AttemptDto> attempts;
}
