package com.contest.sports_programming_server.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskCheckResponse {
    String user_number;
    UUID task_id;
    TestResult[] tests_results;
}
