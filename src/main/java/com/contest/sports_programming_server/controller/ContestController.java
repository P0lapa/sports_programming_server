package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.entity.ContestEntity;
import com.contest.sports_programming_server.service.ContestResultsService;
import com.contest.sports_programming_server.service.ContestService;
import com.contest.sports_programming_server.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contest")
public class ContestController {

    private final ContestService contestService;
    private final ContestResultsService contestResultsService;

    /*===========================ТУРНИРЫ==========================*/

    @GetMapping("/")
    public List<ContestDetailsDto> listTournaments() {
        return contestService.findAllContests();
    }

    @GetMapping("/{id}")
    public ContestDetailsDto getTournamentById(@PathVariable("id") UUID contestId) {
        return contestService.findContestById(contestId);
    }

    @PostMapping("/")
    public ResponseEntity<ContestDetailsDto> createTournament(@RequestBody @Valid CreateContestRequest req) {
        ContestDetailsDto contest = contestService.createContest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(contest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContestDetailsDto> updateTournament(@PathVariable("id") UUID contestId,
                                                              @RequestBody @Valid UpdateContestRequest req) {
        req.setId(contestId);
        ContestDetailsDto contest = contestService.updateContest(req);
        return ResponseEntity.ok(contest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable("id") UUID contestId) {
        contestService.deleteContest(contestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/finalize")
    public void finalizeContest(@PathVariable("id") UUID contestId) {
        contestResultsService.finalizeContest(contestId);
    }

    @PostMapping("/{id}/set-status/{status}")
    public ResponseEntity<Void> setContestStatus(
            @PathVariable("id") UUID contestId,
            @PathVariable("status") ContestStatus status) {
        contestService.setContestStatus(contestId, status);
        return ResponseEntity.noContent().build();
    }
}
