package com.contest.sports_programming_server.controller.admin;

import com.contest.sports_programming_server.dto.ContestParticipantDto;
import com.contest.sports_programming_server.service.ContestParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contest/{id}/participants")
@RequiredArgsConstructor
@Tag(name="Admin_ContestParticipants")
public class ContestParticipantController {

    private final ContestParticipantService contestParticipantService;

    @GetMapping("/")
    public List<ContestParticipantDto> findByContestId(@PathVariable("id") UUID contestId) {
        return contestParticipantService.findByContestIdAsDto(contestId);
    }

}
