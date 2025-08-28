package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
import com.contest.sports_programming_server.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    private final TaskRepository taskRepo;

    private final ParticipantRepository participantRepo;
    private final ContestRepository contestRepo;
    private final SolutionRepository solutionRepo;

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

    /* ===================== РЕШЕНИЯ ===================== */

    // GET /api/admin/solutions?participantId=&taskId=&contestId=
    @GetMapping("/solutions")
    public List<SolutionEntity> listSolutions(@RequestParam(required = false) UUID participantId,
                                              @RequestParam(required = false) UUID taskId,
                                              @RequestParam(required = false) UUID contestId) {
        return solutionRepo.findAllFiltered(participantId, taskId, contestId);
    }

    /* ===================== ЗАДАЧИ ===================== */

    // GET /api/admin/tasks — список задач
    @GetMapping("/tasks")
    public List<TaskListItemDto> listTasks() {
        return adminService.listTasks();
    }

    // POST /api/admin/tasks — создать задачу
    @PostMapping("/tasks")
    public TaskEntity createTask(@RequestBody CreateTaskRequest req) {
        return adminService.createTask(req);
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

    /* ===================== УТИЛИТЫ ===================== */
//
//    private static final SecureRandom RNG = new SecureRandom();
//
//    private static String generateLogin() {
//        // u-<8hex>
//        return "u-" + HexFormat.of().withUpperCase().formatHex(randomBytes(4));
//    }
//
//    private static String generatePassword() {
//        // 12 символов [a-zA-Z0-9]
//        final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        StringBuilder sb = new StringBuilder(12);
//        for (int i = 0; i < 12; i++) sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
//        return sb.toString();
//    }
//
//    private static byte[] randomBytes(int n) {
//        byte[] b = new byte[n];
//        RNG.nextBytes(b);
//        return b;
//    }
}
