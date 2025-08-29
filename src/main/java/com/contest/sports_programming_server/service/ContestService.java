package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskCheckResponse;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.entity.ContestEntity;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import com.contest.sports_programming_server.mapper.TaskMapper;
import com.contest.sports_programming_server.mapper.TestMapper;
import com.contest.sports_programming_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestService {

    private final ContestRepository contestRepository;
    private final ParticipantRepository participantRepository;
    private final ContestParticipantRepository contestParticipantRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TestService testService;
    private final TestMapper testMapper;
    private final AttemptService attemptService;

    @Transactional(readOnly = true)
    public LoginResponse findContest(String login, String password) {
        Optional<ContestParticipantEntity> optionalEntity = contestParticipantRepository.findByLogin(login);
        if (optionalEntity.isEmpty() || !optionalEntity.get().getPassword().equals(password)) {
            log.warn("Invalid login or password for login: {}", login);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        ContestParticipantEntity entity = optionalEntity.get();
        ContestEntity contest = entity.getContest();

        if (hasContestStarted(contest)) {
            log.info("Contest {} has started for login: {}", contest.getId(), login);
            return getLoginResponse(contest, true);
        }

        log.info("Contest {} has not started yet for login: {}", contest.getId(), login);
        return getLoginResponse(contest, false);
    }

    private boolean hasContestStarted(ContestEntity contest) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate contestStartDate = LocalDate.parse(contest.getStartDate(), dateFormatter);
        LocalTime contestStartTime = LocalTime.parse(contest.getStartTime(), timeFormatter);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        return contest.getStatus().equals("STARTED") &&
                (contestStartDate.isBefore(currentDate) ||
                        (contestStartDate.isEqual(currentDate) && contestStartTime.isBefore(currentTime)));
//Если таки переделаем формат в сущности:
//        LocalDateTime contestStart = LocalDateTime.of(contest.getStartDate(), contest.getStartTime());
//        return contest.getStatus().equals("STARTED") && contestStart.isBefore(LocalDateTime.now());
    }

    private LoginResponse getLoginResponse(ContestEntity contest, boolean loadTasks) {
        List<TaskEntity> tasks = loadTasks ? taskRepository.findByContest_Id(contest.getId()) : Collections.emptyList();
        return new LoginResponse(
                contest.getId(),
                contest.getName(),
                contest.getDescription(),
                taskMapper.toShortDtoList(tasks)
        );
    }

    public AttemptDto runOpenTests(TaskCheckRequest request) {

        var contestParticipant = contestParticipantRepository.findById(request.getParticipantId())
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

    public TaskCheckResponse runAllTests(TaskCheckRequest request) {

        var contestParticipant = contestParticipantRepository.findById(request.getParticipantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
        var task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        var tests = testService.getTestsByTask(task.getId());

        return new TaskCheckResponse();
    }
}
