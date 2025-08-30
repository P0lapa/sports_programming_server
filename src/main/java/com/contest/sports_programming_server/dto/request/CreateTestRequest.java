package com.contest.sports_programming_server.dto.request;

import lombok.Data;

@Data
public class CreateTestRequest {
    Boolean isPublic;
    String input;
    String expected;
}
