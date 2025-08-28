package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class CreateContestRequest {

    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String status;
}