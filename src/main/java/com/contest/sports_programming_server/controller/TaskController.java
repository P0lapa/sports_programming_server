package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.request.CreateTaskRequest;
import com.contest.sports_programming_server.dto.request.UpdateTaskRequest;
import com.contest.sports_programming_server.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contest/{contest_id}/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
    public List<TaskDetailsDto> listTasks(@PathVariable("contest_id") UUID contestId) {
        return taskService.findTasksByContestId(contestId);
    }

    @GetMapping("/{task_id}")
    public TaskDetailsDto getTaskById(
            @PathVariable("contest_id") UUID contestId,
            @PathVariable("task_id") UUID taskId) {
        return taskService.findTaskById(taskId);
    }

    @PostMapping("/")
    public ResponseEntity<TaskDetailsDto> createTask(
            @PathVariable("contest_id") UUID contestId,
            @RequestBody @Valid CreateTaskRequest req) {
        TaskDetailsDto task = taskService.createTask(contestId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{task_id}")
    public ResponseEntity<TaskDetailsDto> updateTask(
            @PathVariable("contest_id") UUID contestId,
            @PathVariable("task_id") UUID taskId,
            @RequestBody @Valid UpdateTaskRequest req) {
        req.setId(taskId);
        TaskDetailsDto task = taskService.updateTask(req);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{task_id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("contest_id") UUID contestId,
            @PathVariable("task_id") UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
