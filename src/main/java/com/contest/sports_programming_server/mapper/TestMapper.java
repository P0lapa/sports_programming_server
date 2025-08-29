package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TestCase;
import com.contest.sports_programming_server.entity.TestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestMapper {

    @Mapping(target = "input", source = "inputData")
    @Mapping(target = "expected", source = "expectedOutput")
    List<TestCase> toTCList(List<TestEntity> entities);
    TestCase toTC(TestEntity testEntity);

}
