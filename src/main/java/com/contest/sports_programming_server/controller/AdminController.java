package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.*;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
import com.contest.sports_programming_server.service.AdminService;
import com.contest.sports_programming_server.service.ContestService;
import com.contest.sports_programming_server.service.TaskService;
import com.contest.sports_programming_server.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.core.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "For Admin")
public class AdminController {

    private final AdminService adminService;
    private final TaskService taskService;
    private final ContestService contestService;

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

    /*======================================================================*/

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
