package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TestDto;
import com.contest.sports_programming_server.entity.TestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TestMapper.class})
public interface DtoTestMapper{

    TestDto toDto(TestEntity testEntity);
    List<TestDto> toDtoList(List<TestEntity> testEntities);

    @Mapping(target = "task", ignore = true)
    void updateEntityFromDto(TestDto dto, @MappingTarget TestEntity entity);
}
