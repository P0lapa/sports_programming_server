package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateTaskRequest;
import com.contest.sports_programming_server.dto.request.CreateTestRequest;
import com.contest.sports_programming_server.dto.request.UpdateTaskRequest;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
import com.contest.sports_programming_server.service.AdminService;
import com.contest.sports_programming_server.service.TaskService;
import com.contest.sports_programming_server.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "For Admin")
public class AdminController {

    private final AdminService adminService;
    private final TaskService taskService;
    private final TestService testService;

    private final TaskRepository taskRepo;

    private final ParticipantRepository participantRepo;
    private final ContestRepository contestRepo;
    private final ContestParticipantRepository contestParticipantRepo;
    private final RequestService requestBuilder;

    /* ===================== УЧАСТНИКИ ===================== */

    // GET /api/admin/participants — список
    @GetMapping("/participants")
    public List<ParticipantEntity> listParticipants() {
        return participantRepo.findAll();
    }

    // POST /api/admin/participants — создание с автогенерацией логина/пароля
    @PostMapping("/participants")
    public CreateParticipantResponse createParticipant(@RequestBody CreateParticipantRequest req) {
        return adminService.createParticipantAndJoinContest(req);
    }

    /* ===================== ЗАДАЧИ ===================== */

    @GetMapping("/tasks")
    public List<TaskDetailsDto> listTasks() {
        return taskService.findTasks();
    }

    @GetMapping("/task/{id}")
    public TaskDetailsDto getTaskById(@PathVariable("id") UUID taskId) {
        return taskService.findTaskById(taskId);
    }

    @PostMapping("/task")
    public ResponseEntity<TaskDetailsDto> createTask(@RequestBody @Valid CreateTaskRequest req) {
        TaskDetailsDto task = taskService.createTask(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<TaskDetailsDto> updateTask(@PathVariable("id") UUID taskId,
                                                     @RequestBody @Valid UpdateTaskRequest req) {
        req.setId(taskId);
        TaskDetailsDto task = taskService.updateTask(req);
        return ResponseEntity.ok(task);
    }

    /* ===================== ТЕСТЫ ===================== */

    @GetMapping("/tasks/{id}/tests")
    public List<TestDto> listTests(@PathVariable("id") UUID taskId) {
        return testService.getTestsByTaskAsDto(taskId);
    }

    @PostMapping("/tasks/{id}/tests")
    public ResponseEntity<TestDto> createTest(@PathVariable("id") UUID taskId,
                                              @RequestBody CreateTestRequest request) {
        TestDto testDto = testService.createTestAsDto(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(testDto);
    }

    @PutMapping("/tasks/{id}/tests/{testId}")
    public ResponseEntity<TestDto> updateTest(@PathVariable("id") UUID taskId,
                                              @PathVariable("testId") UUID testId,
                                              @RequestBody TestDto request) {
        TestDto updated = testService.updateTest(testId, request);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/tasks/{id}/tests/{testId}")
    public ResponseEntity<Void> deleteTest(@PathVariable("id") UUID taskId,
                                           @PathVariable("testId") UUID testId) {
        testService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    }

    /* ===================== ТУРНИРЫ ===================== */

    // GET /api/admin/tournaments — список турниров
    @GetMapping("/tournaments")
    public List<ContestEntity> listTournaments() {
        return contestRepo.findAll();
    }

    // POST /api/admin/tournaments — создать турнир
    @PostMapping("/tournaments")
    public ContestEntity createTournament(@RequestBody CreateContestRequest req) {
        return adminService.createTournament(req);
    }

    // GET /api/admin/tournaments/{tournament_id}/participants
    @GetMapping("/tournaments/{id}/participants")
    public List<ContestParticipantDto> listContestParticipants(@PathVariable("id") UUID contestId) {
        return contestParticipantRepo.findAllByContest_IdAsDto(contestId);
    }

    // POST /api/admin/tournaments/{tournament_id}/participants
    @PostMapping("/tournaments/{id}/participants")
    public ContestParticipantDto createContestParticipant(@PathVariable("id") UUID contestId, @RequestBody UUID participantId) {
        return adminService.joinContest(contestId, participantId);
    }

}
