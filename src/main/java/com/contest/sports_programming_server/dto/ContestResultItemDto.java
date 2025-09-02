package com.contest.sports_programming_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContestResultItemDto {
    Integer place;
    String fullName;
    Double result;
}
