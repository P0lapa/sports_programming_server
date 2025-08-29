package com.contest.sports_programming_server.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AttemptDto {

    UUID id;
    UUID participantId;
    UUID taskId;
    Integer attemptNumber;
    Boolean success;
    private LocalDateTime submissionTime;
    private Language language;
    private String solution;
    List<TestResult> testResults;

}
