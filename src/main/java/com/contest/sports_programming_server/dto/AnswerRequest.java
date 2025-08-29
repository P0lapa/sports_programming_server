package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class AnswerRequest {
    String login;
    Enum<Language> language;
    String solution;
}
