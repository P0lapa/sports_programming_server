package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TaskDto;
import com.contest.sports_programming_server.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
//маппер нужен для преобразования сущностей в dto и обратно

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "weight", source = "weight")
    TaskDto toDto(TaskEntity entity);

    List<TaskDto> toDtoList(List<TaskEntity> entities);
}
