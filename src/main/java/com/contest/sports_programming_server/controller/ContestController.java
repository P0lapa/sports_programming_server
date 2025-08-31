package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.ContestDetailsDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.entity.ContestEntity;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contest")
public class ContestController {

    private final ContestService contestService;

    /*===========================ТУРНИРЫ==========================*/

    @GetMapping("/contests")
    public List<ContestDetailsDto> listTournaments() {
        return contestService.findAllContests();
    }

    @GetMapping("/contests/{id}")
    public ContestDetailsDto getTournamentById(@PathVariable("id") UUID contestId) {
        return contestService.findContestById(contestId);
    }

    @PostMapping("/contests")
    public ResponseEntity<ContestDetailsDto> createTournament(@RequestBody @Valid CreateContestRequest req) {
        ContestDetailsDto contest = contestService.createContest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(contest);
    }

    @PutMapping("/contests/{id}")
    public ResponseEntity<ContestDetailsDto> updateTournament(@PathVariable("id") UUID contestId,
                                                              @RequestBody @Valid UpdateContestRequest req) {
        req.setId(contestId);
        ContestDetailsDto contest = contestService.updateContest(req);
        return ResponseEntity.ok(contest);
    }

    @DeleteMapping("/contests/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable("id") UUID contestId) {
        contestService.deleteContest(contestId);
        return ResponseEntity.noContent().build();
    }
}
