package com.contest.sports_programming_server.controller.admin;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestStatusRequest;
import com.contest.sports_programming_server.service.ContestResultsService;
import com.contest.sports_programming_server.service.ContestService;
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
@RequestMapping("/api/admin/contests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
@Tag(name = "Contest")
public class ContestController {

    private final ContestService contestService;
    private final ContestResultsService contestResultsService;

    @GetMapping("/")
    public List<ContestDetailsDto> listTournaments() {
        return contestService.findAllContests();
    }

    @GetMapping("/{contest_id}")
    public ContestDetailsDto getTournamentById(@PathVariable("contest_id") UUID contestId) {
        return contestService.findContestById(contestId);
    }

    @PostMapping("/")
    public ResponseEntity<ContestDetailsDto> createTournament(@RequestBody @Valid CreateContestRequest req) {
        ContestDetailsDto contest = contestService.createContest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(contest);
    }

    @PutMapping("/{contest_id}")
    public ResponseEntity<ContestDetailsDto> updateTournament(@PathVariable("contest_id") UUID contestId,
                                                              @RequestBody @Valid UpdateContestRequest req) {
        req.setId(contestId);
        ContestDetailsDto contest = contestService.updateContest(req);
        return ResponseEntity.ok(contest);
    }

    @DeleteMapping("/{contest_id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable("contest_id") UUID contestId) {
        contestService.deleteContest(contestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contest_id}/finalize")
    public void finalizeContest(@PathVariable("contest_id") UUID contestId) {
        contestResultsService.finalizeContest(contestId);
    }

    @GetMapping("/{contest_id}/results")
    public ResponseEntity<List<ContestResultItemDto>> getContestResults(@PathVariable("contest_id") UUID contestId) {
        return ResponseEntity.ok(contestResultsService.getContestResults(contestId));
    }

    @PatchMapping("/{contest_id}/status")
    public ResponseEntity<Void> setContestStatus(
            @PathVariable("contest_id") UUID contestId,
            @RequestBody @Valid UpdateContestStatusRequest req) {
        contestService.setContestStatus(contestId, req.getContestStatus());
        return ResponseEntity.ok().build();
    }
}
