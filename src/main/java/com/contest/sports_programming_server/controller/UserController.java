package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.request.LoginRequest;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.security.ContestParticipant;
import com.contest.sports_programming_server.service.TaskService;
import com.contest.sports_programming_server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = userService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contest/tasks")
    public ResponseEntity<List<TaskDetailsDto>> getTasksByContest(@AuthenticationPrincipal ContestParticipant principal) {
        List<TaskDetailsDto> tasks = taskService.findTasksByContestId(principal.getContestId());
        return ResponseEntity.ok(tasks);
    }

    //TODO: Сделать здесь эндпоинт для регистрации участников
}
