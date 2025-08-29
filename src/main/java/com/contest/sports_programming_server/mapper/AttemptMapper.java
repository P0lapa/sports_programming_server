package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.AttemptDto;
import com.contest.sports_programming_server.entity.AttemptEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AttemptTestResultMapper.class})
public interface AttemptMapper {

    @Mapping(target = "participantId", source = "participant.id")
    @Mapping(target = "taskId", source = "task.id")
    AttemptDto toDto(AttemptEntity entity);

    List<AttemptDto> toDtoList(List<AttemptEntity> entities);
}
