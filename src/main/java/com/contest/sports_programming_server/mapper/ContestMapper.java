package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.ContestDetailsDto;
import com.contest.sports_programming_server.dto.request.CreateContestRequest;
import com.contest.sports_programming_server.dto.request.UpdateContestRequest;
import com.contest.sports_programming_server.entity.ContestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ContestMapper {
    @Mapping(target = "numberOfTasks", expression = "java(entity.getTasks() != null ? entity.getTasks().size() : 0)")
    ContestDetailsDto toDto(ContestEntity entity);

    List<ContestDetailsDto> toDtoList(List<ContestEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "participants", ignore = true)
    ContestEntity toEntity(CreateContestRequest request);

    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "participants", ignore = true)
    void updateEntityFromDto(UpdateContestRequest request, @MappingTarget ContestEntity entity);
}