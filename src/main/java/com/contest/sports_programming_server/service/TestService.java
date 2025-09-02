package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateTestRequest;
import com.contest.sports_programming_server.entity.TaskEntity;
import com.contest.sports_programming_server.entity.TestEntity;
import com.contest.sports_programming_server.mapper.DtoTestMapper;
import com.contest.sports_programming_server.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {
    //Очень большой сервис, давай разделим 1 будет только заниматься запуском тестов, а другой, например AttemptService
    // будет вызывать функции отсюда
    private final TestRepository repository;
    private final DtoTestMapper dtoMapper;
    private final TaskService taskService;

    @Transactional(readOnly = true)
    public List<TestEntity> getPublicTestsByTask(UUID taskId) {
        return repository.findAllByTask_IdAndIsPublicTrue(taskId);
    }

    @Transactional(readOnly = true)
    public List<TestEntity> getTestsByTask(UUID taskId) {
        return repository.findAllByTask_Id(taskId);
    }

    @Transactional(readOnly = true)
    public List<TestDto> getTestsByTaskAsDto(UUID taskId) {
        return dtoMapper.toDtoList(getTestsByTask(taskId));
    }

    @Transactional
    public TestEntity createTest(UUID taskId, Boolean isPublic, String input, String expected) {

        TaskEntity task = taskService.getTaskOrThrow(taskId);

        int testNumber = repository.countByTask_Id(taskId) + 1;

        var entity =  TestEntity.builder()
                .task(task)
                .testNumber(testNumber)
                .isPublic(isPublic)
                .inputData(input)
                .expectedOutput(expected)
                .build();
        return repository.save(entity);
    }

    @Transactional
    public TestDto createTestAsDto(UUID taskId, CreateTestRequest request) {
        TestEntity entity = createTest(taskId, request.getIsPublic(), request.getInput(), request.getExpected());
        return dtoMapper.toDto(entity);
    }

    @Transactional
    public TestDto updateTest(UUID testId, TestDto request) {
        TestEntity entity = getTestOrThrow(testId);

        dtoMapper.updateEntityFromDto(request, entity);

        TestEntity saved = repository.save(entity);
        return dtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteTest(UUID testId) {
        if(!repository.existsById(testId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found");
        }
        repository.deleteById(testId);
    }

    public TestEntity getTestOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));
    }

}