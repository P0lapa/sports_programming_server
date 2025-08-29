package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskCheckResponse;
import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.service.ContestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = contestService.findContest(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response); // HTTP 200 с LoginResponse
    }

//    @GetMapping("/{contest_id}")
//    public ResponseEntity<GettingContestResponse> getContest(@PathVariable UUID contestId) {
//
//    }
//
//    // Получение задач
//    @GetMapping("{contest_id}/tasks")
//    public TasksResponse getProblem(@PathVariable UUID contest_id) {
////        return contestService.getAllProblems(id);
//        return null;
//    }
//
//    // Получение задачи по id
//    @GetMapping("{contest_id}/tasks/{id}")
//    public TaskDto getProblem(@PathVariable UUID contest_id, @PathVariable UUID id) {
////        return contestService.getProblem(contest_id, id);
//        return null;
//    }
//
//    @PutMapping("/tasks/{id}")
//    public Object tryTheTask() {
//        return null;
//    }
//
//    @PostMapping("{contest_id}/tasks/{id}")
//    public AnswerRequest getProblem(@PathVariable UUID contest_id, @PathVariable UUID id) {
//        return new AnswerRequest();
//    }

    @PostMapping("/task-check")
    public ResponseEntity<AttemptDto> taskCheck(@RequestBody TaskCheckRequest request) {
        var response = contestService.runOpenTests(request);
        return ResponseEntity.ok(response);
    }


}
