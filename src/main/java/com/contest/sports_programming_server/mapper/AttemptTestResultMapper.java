package com.contest.sports_programming_server.mapper;

import com.contest.sports_programming_server.dto.TestResult;
import com.contest.sports_programming_server.entity.AttemptTestResultEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttemptTestResultMapper {
    TestResult toDto(AttemptTestResultEntity entity);
}
