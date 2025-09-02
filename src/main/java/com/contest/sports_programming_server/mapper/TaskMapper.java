package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.dto.TaskStatus;
import com.contest.sports_programming_server.dto.request.UpdateTaskRequest;
import com.contest.sports_programming_server.entity.AttemptEntity;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

    TaskDetailsDto toDto(TaskEntity entity);
    List<TaskDetailsDto> toDtoList(List<TaskEntity> entities);

    // Для обновления TaskEntity из UpdateTaskRequest
    @Mapping(target = "contest", ignore = true)
    @Mapping(target = "tests", ignore = true)
    void updateEntityFromDto(UpdateTaskRequest dto, @MappingTarget TaskEntity entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "order", ignore = true)
    TaskDto toDtoWithAttempts(TaskEntity task, List<AttemptEntity> attempts);


    // --- AfterMapping для вычисления статуса ---
    @AfterMapping
    default void setStatus(TaskEntity task,
                           List<AttemptEntity> attempts,
                           @MappingTarget TaskDto dto) {
        if (attempts == null || attempts.isEmpty()) {
            dto.setStatus(TaskStatus.NOT_STARTED);
        } else if (attempts.stream().anyMatch(AttemptEntity::getSuccess)) {
            dto.setStatus(TaskStatus.COMPLETED);
        } else {
            dto.setStatus(TaskStatus.FAILED);
        }
    }
}