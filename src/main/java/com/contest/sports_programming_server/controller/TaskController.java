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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;

    /* ===================== ЗАДАЧИ ===================== */

    //TODO: добавить эндпоинт для получения всех задач соревнования. только UUID

    @GetMapping("/tasks")
    public List<TaskDetailsDto> listTasks() {
        return taskService.findTasks();
    }

    @GetMapping("/tasks/{id}")
    public TaskDetailsDto getTaskById(@PathVariable("id") UUID taskId) {
        return taskService.findTaskById(taskId);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDetailsDto> createTask(@RequestBody @Valid CreateTaskRequest req) {
        TaskDetailsDto task = taskService.createTask(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDetailsDto> updateTask(@PathVariable("id") UUID taskId,
                                                     @RequestBody @Valid UpdateTaskRequest req) {
        req.setId(taskId);
        TaskDetailsDto task = taskService.updateTask(req);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
