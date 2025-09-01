package com.contest.sports_programming_server.dto;

import java.util.UUID;

public record ParticipantDto(UUID id, String email, String fullName) {
}
