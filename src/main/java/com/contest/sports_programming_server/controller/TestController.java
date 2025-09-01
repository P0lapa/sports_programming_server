package com.contest.sports_programming_server.controller;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TestDto;
import com.contest.sports_programming_server.dto.request.CreateTestRequest;
import com.contest.sports_programming_server.service.TestService;
import com.contest.sports_programming_server.service.TestingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tasks/{id}/tests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tests")
public class TestController {

    private final TestService testService;

    @GetMapping("/")
    public List<TestDto> listTests(@PathVariable("id") UUID taskId) {
        return testService.getTestsByTaskAsDto(taskId);
    }

    @PostMapping("/")
    public ResponseEntity<TestDto> createTest(@PathVariable("id") UUID taskId,
                                              @RequestBody CreateTestRequest request) {
        TestDto testDto = testService.createTestAsDto(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(testDto);
    }

    @PutMapping("/{testId}")
    public ResponseEntity<TestDto> updateTest(@PathVariable("id") UUID taskId,
                                              @PathVariable("testId") UUID testId,
                                              @RequestBody TestDto request) {
        TestDto updated = testService.updateTest(testId, request);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<Void> deleteTest(@PathVariable("id") UUID taskId,
                                           @PathVariable("testId") UUID testId) {
        testService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    }
}
