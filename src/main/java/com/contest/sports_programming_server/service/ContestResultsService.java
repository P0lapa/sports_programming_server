package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.ContestResultItemDto;
import com.contest.sports_programming_server.dto.Solution;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.AttemptRepository;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import com.contest.sports_programming_server.repository.ContestParticipantTaskResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class ContestResultsService {

    private final ContestParticipantService contestParticipantService;
    private final ContestService contestService;
    private final AttemptRepository attemptRepository;
    private final ContestParticipantTaskResultRepository contestParticipantTaskResultRepository;
    private final JudgeService judgeService;
    private final ContestParticipantRepository contestParticipantRepository;

    @Transactional
    public void finalizeContest(UUID contestId) {

        log.debug("Finalizing contest {}", contestId);

        ContestEntity contest = contestService.getContestOrThrow(contestId);

        log.debug("Contest got");

        List<AttemptEntity> allAttempts = attemptRepository.findByTask_Contest_Id(contestId);

        Map<UUID, Map<UUID, List<AttemptEntity>>> attemptsByParticipantAndTask =
                allAttempts.stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getParticipant().getId(),
                                Collectors.groupingBy(a -> a.getTask().getId())
                        ));

        log.debug("Attempts got");

        List<ContestParticipantTaskResultEntity> resultsToSave = new ArrayList<>();

        double participantScore = 0;

        for (var participant : contest.getParticipants()) {

            log.debug("Start counting for participant {}", participant.getId());

            contestParticipantService.finishContestForParticipant(participant);

            for (var task : contest.getTasks()) {

                log.debug("Start counting for task {}", task.getId());

                List<AttemptEntity> attempts =
                        attemptsByParticipantAndTask
                                .getOrDefault(participant.getId(), Map.of())
                                .getOrDefault(task.getId(), List.of());

                ContestParticipantTaskResultEntity result = new ContestParticipantTaskResultEntity();
                result.setParticipant(participant);
                result.setTask(task);

                if (attempts.isEmpty()) {
                    log.debug("No attempts found for participant {}", participant.getId());
                    result.setAttemptsCount(0);
                    result.setPassedTestsCount(0);
                    result.setScore(0);
                } else {

                    AttemptEntity lastAttempt = attempts.stream()
                            .max(Comparator.comparing(AttemptEntity::getAttemptNumber))
                            .orElseThrow();

                    result.setAttemptsCount(lastAttempt.getAttemptNumber());

                    log.debug("Starting tests for attempt: {}", lastAttempt.getId());
                    AttemptEntity finalAttempt = judgeService.runAllTests(
                            participant,
                            task,
                            new Solution(lastAttempt.getSolution(), lastAttempt.getLanguage())
                    );
                    log.debug("Tests done");
                    result.setPassedTestsCount(finalAttempt.getPassedTestsCount());
                    result.setScore(calculateScore(result, task));
                }

                participantScore += result.getScoreAsDouble();
                resultsToSave.add(result);
                log.debug("Finished counting for id {}", task.getId());
            }

            participant.setResult(participantScore);
            participantScore = 0;
            log.debug("Finished counting for participant {}", participant.getId());
        }

        log.debug("Saving results");
        contestParticipantTaskResultRepository.saveAll(resultsToSave);
        log.debug("Saving results done");

    }

    private Double calculateScore(ContestParticipantTaskResultEntity result, TaskEntity task) {
        int taskTestsCount = task.getTests().size();
        if(taskTestsCount == result.getPassedTestsCount()){
            return (double) task.getWeight();
        } else {
            return (double) result.getPassedTestsCount() * task.getWeight() / (taskTestsCount * 2) ;
        }
    }

    @Transactional(readOnly = true)
    public List<ContestResultItemDto> getContestResults(UUID contestId) {
        List<ContestParticipantEntity> participants =
                contestParticipantRepository.findByContestIdWithParticipantOrderByResultDesc(contestId);

        AtomicInteger placeCounter = new AtomicInteger(1);

        return participants.stream()
                .sorted(Comparator.comparing(ContestParticipantEntity::getResult,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(p -> new ContestResultItemDto(placeCounter.getAndIncrement(), p.getParticipant().getFullName(), p.getResultAsDouble()))
                .toList();
    }
}
