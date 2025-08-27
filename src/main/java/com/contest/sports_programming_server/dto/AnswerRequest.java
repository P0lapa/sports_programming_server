package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class AnswerRequest {
    String login;
    String language;
    String solution;
}
