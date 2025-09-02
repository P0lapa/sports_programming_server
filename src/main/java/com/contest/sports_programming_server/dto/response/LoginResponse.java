package com.contest.sports_programming_server.dto.response;

import com.contest.sports_programming_server.dto.ContestParticipantShortDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class LoginResponse {
    private final ContestParticipantShortDto user;
    private String token;
}
