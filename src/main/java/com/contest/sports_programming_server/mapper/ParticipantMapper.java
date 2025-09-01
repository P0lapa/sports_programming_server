package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.ParticipantDto;
import com.contest.sports_programming_server.entity.ParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ParticipantMapper {

    ParticipantDto toDto(ParticipantEntity entity);
    List<ParticipantDto> toDto(List<ParticipantEntity> entities);

}
