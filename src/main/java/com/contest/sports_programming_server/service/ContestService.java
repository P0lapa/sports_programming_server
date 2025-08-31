package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.entity.ContestEntity;
import com.contest.sports_programming_server.mapper.ContestMapper;
import com.contest.sports_programming_server.mapper.TaskMapper;
import com.contest.sports_programming_server.mapper.TestMapper;
import com.contest.sports_programming_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestService {

    private final ContestRepository contestRepository;
    private final TaskRepository taskRepository;

    private final ContestMapper contestMapper;

    @Transactional(readOnly = true)
    public List<ContestDetailsDto> findAllContests() {
        log.debug("Fetching all contests");
        List<ContestEntity> contests = contestRepository.findAllWithTasks();
        return contestMapper.toDtoList(contests);
    }

    @Transactional(readOnly = true)
    public ContestDetailsDto findContestById(UUID id) {
        log.debug("Fetching contest with id: {}", id);
        ContestEntity contest = contestRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + id));
        return contestMapper.toDto(contest);
    }

    @Transactional
    public ContestDetailsDto createContest(CreateContestRequest request) {
        log.debug("Creating contest with name: {}", request.getName());
        ContestEntity contest = contestMapper.toEntity(request);
        contest = contestRepository.save(contest);
        log.info("Created contest with id: {}", contest.getId());
        return contestMapper.toDto(contest);
    }

    @Transactional
    public ContestDetailsDto updateContest(UpdateContestRequest request) {
        log.debug("Updating contest with id: {}", request.getId());
        ContestEntity contest = contestRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + request.getId()));
        contestMapper.updateEntityFromDto(request, contest);
        contest = contestRepository.save(contest);
        log.info("Updated contest with id: {}", contest.getId());
        Hibernate.initialize(contest.getTasks());
        return contestMapper.toDto(contest);
    }

    @Transactional
    public void deleteContest(UUID id) {
        log.debug("Deleting contest with id: {}", id);
        if (!contestRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + id);
        }
        if (taskRepository.existsByContest_Id(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete contest with existing tasks");
        }
        contestRepository.deleteById(id);
        log.info("Deleted contest with id: {}", id);
    }
}
