package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.dto.request.CreateTaskRequest;
import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.request.UpdateTaskRequest;
import com.contest.sports_programming_server.entity.ContestEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import com.contest.sports_programming_server.mapper.TaskMapper;
import com.contest.sports_programming_server.repository.ContestRepository;
import com.contest.sports_programming_server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ContestRepository contestRepository;

    private final TaskMapper taskMapper;

    @Transactional(readOnly = true)
    public List<TaskDetailsDto> findTasks() {
        List<TaskEntity> tasks = taskRepository.findAll();
        return taskMapper.toDtoList(tasks);
    }

    @Transactional(readOnly = true)
    public TaskDetailsDto findTaskById(UUID id) {
        TaskEntity task = getTaskOrThrow(id);
        return taskMapper.toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDetailsDto> findTasksByContestId(UUID contestId) {
        List<TaskEntity> tasks = taskRepository.findByContest_Id(contestId);
        return taskMapper.toDtoList(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getTasksForParticipant(UUID contestId, UUID participantId) {

        List<TaskDto> tasks = taskRepository.findTaskDtosForParticipant(contestId, participantId);

        AtomicInteger counter = new AtomicInteger(1);
        tasks.forEach(t -> t.setOrder(counter.getAndIncrement()));

        return tasks;
    }

    @Transactional
    public TaskDetailsDto createTask(UUID contestId, CreateTaskRequest createTaskRequest) {
        ContestEntity contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contest not found with id: " + contestId));

        TaskEntity task = TaskEntity.builder()
                .name(createTaskRequest.getName())
                .description(createTaskRequest.getDescription())
                .timeLimit(createTaskRequest.getTimeLimit())
                .memoryLimit(createTaskRequest.getMemoryLimit())
                .weight(createTaskRequest.getWeight())
                .contest(contest)
                .build();

        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskDetailsDto updateTask(UpdateTaskRequest updateTaskRequest) {
        TaskEntity task = getTaskOrThrow(updateTaskRequest.getId());

        taskMapper.updateEntityFromDto(updateTaskRequest, task);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public TaskEntity getTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: " + id));
    }
}
