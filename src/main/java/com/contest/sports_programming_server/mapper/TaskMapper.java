package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TaskDetailsDto;
import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.dto.request.UpdateTaskRequest;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {
    // Для короткого DTO (TaskDto)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "weight", source = "weight")
    TaskDto toShortDto(TaskEntity entity);

    List<TaskDto> toShortDtoList(List<TaskEntity> entities);

    // Для полного DTO (TaskDetailsDto)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "timeLimit", source = "timeLimit")
    @Mapping(target = "memoryLimit", source = "memoryLimit")
    @Mapping(target = "weight", source = "weight")
    TaskDetailsDto toDto(TaskEntity entity);

    List<TaskDetailsDto> toDtoList(List<TaskEntity> entities);

    // Для обновления TaskEntity из UpdateTaskRequest
    @Mapping(target = "contest", ignore = true)
    @Mapping(target = "tests", ignore = true)
    void updateEntityFromDto(UpdateTaskRequest dto, @MappingTarget TaskEntity entity);
}