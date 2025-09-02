package com.contest.sports_programming_server.controller.admin;

import com.contest.sports_programming_server.dto.TestDto;
import com.contest.sports_programming_server.dto.request.CreateTestRequest;
import com.contest.sports_programming_server.service.TestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tasks/{task_id}/tests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
@Tag(name = "Tests")
public class TestController {

    private final TestService testService;

    @GetMapping("/")
    public List<TestDto> listTests(@PathVariable("task_id") UUID taskId) {
        return testService.getTestsByTaskAsDto(taskId);
    }

    @PostMapping("/")
    public ResponseEntity<TestDto> createTest(@PathVariable("task_id") UUID taskId,
                                              @RequestBody CreateTestRequest request) {
        TestDto testDto = testService.createTestAsDto(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(testDto);
    }

    @PutMapping("/{test_id}")
    public ResponseEntity<TestDto> updateTest(@PathVariable("task_id") UUID taskId,
                                              @PathVariable("test_id") UUID testId,
                                              @RequestBody TestDto request) {
        TestDto updated = testService.updateTest(testId, request);
        return ResponseEntity.ok(updated);

    }

    @DeleteMapping("/{test_id}")
    public ResponseEntity<Void> deleteTest(@PathVariable("task_id") UUID taskId,
                                           @PathVariable("test_id") UUID testId) {
        testService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    }
}
