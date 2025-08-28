package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
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

    private final ParticipantRepository participantRepo;
    private final TaskRepository taskRepo;
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
    @Transactional
    public CreateParticipantResponse createParticipant(@RequestBody @Valid CreateParticipantRequest req) {
        String login = generateLogin();
        while (participantRepo.existsByLogin(login)) login = generateLogin();

        String password = generatePassword();

        ParticipantEntity p = ParticipantEntity.builder()
                .fullName(req.getFullName())
                .email(req.getContact())
                .login(login)
                .email(req.getContact())   // если это почта — ок; если тг — останется как есть
                .password(password)     // в проде хешируй
                .build();

        participantRepo.save(p);
        return new CreateParticipantResponse(p.getId(), login, password);
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
    public List<TaskEntity> listTasks() {
        return taskRepo.findAll();
    }

    // POST /api/admin/tasks — создать задачу
    @PostMapping("/tasks")
    @Transactional
    public TaskEntity createTask(@RequestBody @Valid CreateTaskRequest req) {
        ContestEntity contest = contestRepo.findById(req.getContestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contestId not found"));

        TaskEntity t = TaskEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .contest(contest)
                .build();

        return taskRepo.save(t);
    }

    /* ===================== ТУРНИРЫ ===================== */

    // GET /api/admin/tournaments — список турниров
    @GetMapping("/tournaments")
    public List<ContestEntity> listTournaments() {
        return contestRepo.findAll();
    }

    // POST /api/admin/tournaments — создать турнир
    @PostMapping("/tournaments")
    @Transactional
    public ContestEntity createTournament(@RequestBody @Valid CreateContestRequest req) {
        var c = ContestEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus())
                .build();
        return contestRepo.save(c);
    }

    /* ===================== УТИЛИТЫ ===================== */

    private static final SecureRandom RNG = new SecureRandom();

    private static String generateLogin() {
        // u-<8hex>
        return "u-" + HexFormat.of().withUpperCase().formatHex(randomBytes(4));
    }

    private static String generatePassword() {
        // 12 символов [a-zA-Z0-9]
        final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
        return sb.toString();
    }

    private static byte[] randomBytes(int n) {
        byte[] b = new byte[n];
        RNG.nextBytes(b);
        return b;
    }
}
