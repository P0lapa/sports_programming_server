package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.service.ContestService;
import com.contest.sports_programming_server.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "For Participants")
public class ContestController {

    private final ContestService contestService;
    private final TaskService taskService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = contestService.findContest(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response); // HTTP 200 с LoginResponse
    }

    @GetMapping("/{contest_id}/tasks")
    public ResponseEntity<List<TaskDetailsDto>> getTasksByContest(@PathVariable("contest_id") UUID contestId) {
        List<TaskDetailsDto> tasks = taskService.findTasksByContestId(contestId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{contest_id}/task/{id}")
    public ResponseEntity<TaskDetailsDto> getTaskById(@PathVariable("contest_id") UUID contestId,
                                                      @PathVariable("id") UUID taskId) {
        TaskDetailsDto task = taskService.findTaskById(taskId);
        if (taskService.findTasksByContestId(contestId).stream().noneMatch(t -> t.getId().equals(taskId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task does not belong to contest: " + contestId);
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("/task-check")
    public ResponseEntity<AttemptDto> taskCheck(@RequestBody TaskCheckRequest request) {
        var response = contestService.runOpenTests(request);
        return ResponseEntity.ok(response);
    }


}
