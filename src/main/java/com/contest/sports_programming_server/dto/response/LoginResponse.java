package com.contest.sports_programming_server.dto.response;

import com.contest.sports_programming_server.dto.TaskDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class LoginResponse {
//передаётся когда соревнования уже начались
    private UUID contestId;
    private String name;
    private String description;
    //какие ещё поля о соревнованнии передать
    private List<TaskDto> tasks;
    private String token;
}
