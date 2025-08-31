package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.service.TestService;
import com.contest.sports_programming_server.service.TestingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contest")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tests_IDK")
public class TestController {

    private final TestingService testingService;

    @PostMapping("/task-check")
    public ResponseEntity<AttemptDto> taskCheck(@RequestBody TaskCheckRequest request) {
        var response = testingService.runOpenTests(request);
        return ResponseEntity.ok(response);
    }
}
