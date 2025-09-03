package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.dto.response.GetContestInfoResponse;
import com.contest.sports_programming_server.entity.ContestEntity;
import com.contest.sports_programming_server.mapper.ContestMapper;
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
@Slf4j(topic = "ContestLogger")
public class ContestService {

    private final ContestRepository contestRepository;
    private final TaskRepository taskRepository;

    private final ContestMapper contestMapper;

    @Transactional(readOnly = true)
    public List<ContestDetailsDto> findAllContests() {
        List<ContestEntity> contests = contestRepository.findAllWithTasks();
        return contestMapper.toDtoList(contests);
    }

    @Transactional(readOnly = true)
    public ContestDetailsDto findContestById(UUID id) {
        ContestEntity contest = contestRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + id));
        return contestMapper.toDto(contest);
    }

    @Transactional
    public ContestDetailsDto createContest(CreateContestRequest request) {
        ContestEntity contest = contestMapper.toEntity(request);
        contest = contestRepository.save(contest);
        return contestMapper.toDto(contest);
    }

    @Transactional
    public ContestDetailsDto updateContest(UpdateContestRequest request) {
        ContestEntity contest = contestRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + request.getId()));
        contestMapper.updateEntityFromDto(request, contest);
        contest = contestRepository.save(contest);
        Hibernate.initialize(contest.getTasks());
        return contestMapper.toDto(contest);
    }

    @Transactional
    public void deleteContest(UUID id) {
        if (!contestRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + id);
        }
        if (taskRepository.existsByContest_Id(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete contest with existing tasks");
        }
        contestRepository.deleteById(id);
    }

    public ContestEntity getContestOrThrow(UUID id) {
        return contestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + id));
    }

    public void setContestStatus(UUID id, ContestStatus status) {
        var contest = getContestOrThrow(id);
        contest.setContestStatus(status);
        contestRepository.save(contest);
        log.debug("Contest {} status updated to {}", contest.getId(), contest.getContestStatus());
    }

    public GetContestInfoResponse getContestInfo(UUID contestId) {
        ContestEntity contest = getContestOrThrow(contestId);
        return new GetContestInfoResponse(contest.getName(), contest.getDescription());
    }


}
