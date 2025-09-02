package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.dto.Language;
import com.contest.sports_programming_server.dto.TestResult;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.mapper.AttemptMapper;
import com.contest.sports_programming_server.repository.AttemptRepository;
import com.contest.sports_programming_server.repository.AttemptTestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;

    @Transactional
    public AttemptEntity addAttempt(
            ContestParticipantEntity participant,
            TaskEntity task,
            Language language,
            String solution,
            List<TestEntity> tests,
            List<TestResult> results
    ) {

        int attemptNumber = getNextAttemptNumber(participant.getId(), task.getId());

        var attempt = AttemptEntity.builder()
                .participant(participant)
                .task(task)
                .attemptNumber(attemptNumber)
                .success(isAttemptSuccessful(results))
                .language(language)
                .solution(solution)
                .build();

        int loopSize = Integer.min(results.size(), tests.size());

        for(int i = 0; i < loopSize; i++) {

            TestResult testResult = results.get(i);
            TestEntity testEntity = tests.get(i);

            AttemptTestResultEntity result = AttemptTestResultEntity.builder()
                    .attempt(attempt)
                    .test(testEntity)
                    .passed(testResult.getPassed())
                    .reason(testResult.getReason())
                    .timeSeconds(testResult.getTimeSeconds())
                    .memoryBytes(testResult.getMemoryBytes())
                    .build();
            attempt.getTestResults().add(result);
        }

        return attemptRepository.save(attempt);

    }

    private boolean isAttemptSuccessful(List<TestResult> results) {
        return results.stream().noneMatch(result -> Boolean.FALSE.equals(result.getPassed()));
    }

    private int getNextAttemptNumber(UUID participantId, UUID taskId) {
        return attemptRepository.countByParticipant_IdAndTask_Id(participantId, taskId) + 1;
    }

}
