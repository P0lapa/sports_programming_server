package com.contest.sports_programming_server.mapper;


import com.contest.sports_programming_server.dto.ContestParticipantDto;
import com.contest.sports_programming_server.dto.NewContestParticipantDto;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestParticipantMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "login", source = "entity.login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "contestId", source = "entity.contest.id")
    @Mapping(target = "participantId", source = "entity.participant.id")
    NewContestParticipantDto toDTO(ContestParticipantEntity entity, String password);

    @Mapping(target = "contestId", source = "contest.id")
    @Mapping(target = "participantId", source = "participant.id")
    ContestParticipantDto toDTO(ContestParticipantEntity entity);
    List<ContestParticipantDto> toDTOList(List<ContestParticipantEntity> entities);
}
