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
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "For Participants")
public class ContestController {

    private final ContestService contestService;
    private final TaskService taskService;



    @GetMapping("/{contest_id}/tasks")
    public ResponseEntity<List<TaskDetailsDto>> getTasksByContest(@PathVariable("contest_id") UUID contestId) {
        List<TaskDetailsDto> tasks = taskService.findTasksByContestId(contestId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{contest_id}/tasks/{id}")
    public ResponseEntity<TaskDetailsDto> getTaskById(@PathVariable("contest_id") UUID contestId,
                                                      @PathVariable("id") UUID taskId) {
        TaskDetailsDto task = taskService.findTaskById(taskId);
        if (taskService.findTasksByContestId(contestId).stream().noneMatch(t -> t.getId().equals(taskId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task does not belong to contest: " + contestId);
        }
        return ResponseEntity.ok(task);
    }


    /*===========================ТУРНИРЫ==========================*/

    @GetMapping("/tournaments")
    public List<ContestDetailsDto> listTournaments() {
        return contestService.findAllContests();
    }

    @GetMapping("/tournaments/{id}")
    public ContestDetailsDto getTournamentById(@PathVariable("id") UUID contestId) {
        return contestService.findContestById(contestId);
    }

    @PostMapping("/tournaments")
    public ResponseEntity<ContestDetailsDto> createTournament(@RequestBody @Valid CreateContestRequest req) {
        ContestDetailsDto contest = contestService.createContest(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(contest);
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<ContestDetailsDto> updateTournament(@PathVariable("id") UUID contestId,
                                                              @RequestBody @Valid UpdateContestRequest req) {
        req.setId(contestId);
        ContestDetailsDto contest = contestService.updateContest(req);
        return ResponseEntity.ok(contest);
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable("id") UUID contestId) {
        contestService.deleteContest(contestId);
        return ResponseEntity.noContent().build();
    }
}
