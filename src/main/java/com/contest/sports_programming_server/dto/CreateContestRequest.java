package com.contest.sports_programming_server.dto;

import lombok.Data;

@Data
public class CreateContestRequest {

    String name;
    String description;
    String startDate;
    String endDate;
    String startTime;
    String endTime;
    String status;
}
