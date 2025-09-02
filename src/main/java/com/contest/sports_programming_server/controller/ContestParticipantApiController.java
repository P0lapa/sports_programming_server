package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.security.ContestParticipant;
import com.contest.sports_programming_server.service.ContestParticipantApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name="Participant API")
public class ContestParticipantApiController {

    private final ContestParticipantApiService contestParticipantApiService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(@AuthenticationPrincipal ContestParticipant principal) {
        var tasks = contestParticipantApiService.getTasks(principal);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/task-check")
    public ResponseEntity<AttemptDto> taskCheck(
            @AuthenticationPrincipal ContestParticipant principal,
            @RequestBody TaskCheckRequest request) {
        var attempt = contestParticipantApiService.taskCheck(principal, request);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/finish")
    public ResponseEntity<Void> finishContest(@AuthenticationPrincipal ContestParticipant principal) {
        contestParticipantApiService.finishContest(principal);
        return ResponseEntity.noContent().build();
    }
}
