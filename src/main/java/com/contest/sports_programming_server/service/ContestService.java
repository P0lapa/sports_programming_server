package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskCheckResponse;

public class ContestService {

    public TaskCheckResponse runOpenTests(TaskCheckRequest request) {
        return new TaskCheckResponse();
    }

    public TaskCheckResponse runAllTests(TaskCheckRequest request) {

        // Получить Task с помошью request.task_id
        //
        return new TaskCheckResponse();
    }
}
