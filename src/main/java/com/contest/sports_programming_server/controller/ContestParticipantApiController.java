package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.security.ContestParticipant;
import com.contest.sports_programming_server.service.TaskService;
import com.contest.sports_programming_server.service.TestingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Participant API")
public class ContestParticipantApiController {

    private final TaskService taskService;
    private final TestingService testingService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDetailsDto>> getTasksByContest(@AuthenticationPrincipal ContestParticipant principal) {
        List<TaskDetailsDto> tasks = taskService.findTasksByContestId(principal.getContestId());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/task-check")
    public ResponseEntity<AttemptDto> taskCheck(@RequestBody TaskCheckRequest request) {
        var attempt = testingService.runOpenTests(request);
        return ResponseEntity.ok(attempt);
    }
}
