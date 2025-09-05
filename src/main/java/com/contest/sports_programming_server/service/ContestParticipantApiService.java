package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.response.GetContestInfoResponse;
import com.contest.sports_programming_server.mapper.AttemptMapper;
import com.contest.sports_programming_server.security.ContestParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContestParticipantApiService {

    private final TaskService taskService;
    private final AttemptMapper attemptMapper;
    private final JudgeService judgeService;
    private final ContestParticipantService contestParticipantService;
    private final AttemptService attemptService;
    private final ContestService contestService;

    @Transactional(readOnly = true)
    public List<TaskDto> getTasks(ContestParticipant principal) {
        return taskService.getTasksForParticipant(principal.getContestId(), principal.getId());
    }

    @Transactional
    public AttemptDto taskCheck(ContestParticipant principal, TaskCheckRequest request) {
        return attemptMapper.toDto(judgeService.runOpenTests(principal.getId(), request));
    }

    @Transactional
    public void finishContest(ContestParticipant principal) {
        contestParticipantService.finishContestForParticipant(principal.getId());
    }

    @Transactional
    public ContestParticipantShortDto getProfile(ContestParticipant principal) {
        return new ContestParticipantShortDto(principal);
    }

    @Transactional
    public TaskWithAttemptsDto getTaskWithAttempts(ContestParticipant principal, UUID taskId) {
        var taskDto = taskService.findTaskById(taskId);
        var attempts = attemptMapper.toDtoList(attemptService.getAttempts(principal.getId(), taskId));
        return TaskWithAttemptsDto.builder()
                .task(taskDto)
                .attempts(attempts)
                .build();
    }

    public GetContestInfoResponse getContestInfo(ContestParticipant principal) {
        return contestService.getContestInfo(principal.getContestId());
    }

}
