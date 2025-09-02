package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.ContestParticipantShortDto;
import com.contest.sports_programming_server.dto.TaskCheckRequest;
import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.mapper.AttemptMapper;
import com.contest.sports_programming_server.security.ContestParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestParticipantApiService {

    private final TaskService taskService;
    private final AttemptMapper attemptMapper;
    private final JudgeService judgeService;
    private final ContestParticipantService contestParticipantService;

    public List<TaskDto> getTasks(ContestParticipant principal) {
        return taskService.getTasksForParticipant(principal.getContestId(), principal.getId());
    }

    public AttemptDto taskCheck(ContestParticipant principal, TaskCheckRequest request) {
        return attemptMapper.toDto(judgeService.runOpenTests(principal.getId(), request));
    }

    public void finishContest(ContestParticipant principal) {
        contestParticipantService.finishContestForParticipant(principal.getId());
    }

    public ContestParticipantShortDto getProfile(ContestParticipant principal) {
        return new ContestParticipantShortDto(principal);
    }

}
