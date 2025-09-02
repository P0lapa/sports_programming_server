package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.mapper.TestMapper;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import com.contest.sports_programming_server.repository.TaskRepository;
import com.contest.sports_programming_server.security.ContestParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestingService {

    private final ContestParticipantRepository contestParticipantRepository;
    private final TaskRepository taskRepository;
    private final TestService testService;
    private final TestMapper testMapper;
    private final AttemptService attemptService;

    public AttemptDto runOpenTests(ContestParticipant principal, TaskCheckRequest request) {

        var contestParticipant = contestParticipantRepository.findById(principal.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));

        // TODO: add check for contest status

        var task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        var tests = testService.getPublicTestsByTask(task.getId());

        var testResults = testService.runTests(
                contestParticipant.getLogin(),
                request.getLanguage(),
                task.getMemoryLimit(),
                task.getTimeLimit(),
                request.getSolution(),
                testMapper.toTCList(tests)
        );

        return attemptService.addAttempt(
                contestParticipant,
                task,
                request.getLanguage(),
                request.getSolution(),
                tests,
                testResults
        );
    }

//    public TaskCheckResponse runAllTests(TaskCheckRequest request) {
//
//        var contestParticipant = contestParticipantRepository.findById(request.getParticipantId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
//        var task = taskRepository.findById(request.getTaskId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
//
//        var tests = testService.getTestsByTask(task.getId());
//
//        return new TaskCheckResponse();
//    }
}
