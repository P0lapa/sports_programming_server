package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class TestResult {
    Boolean passed = false;
    String reason;
    Double timeSeconds = 0.0;
    Long memoryBytes = 0L;
}
