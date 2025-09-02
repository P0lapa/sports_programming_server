package com.contest.sports_programming_server.controller.admin;

import com.contest.sports_programming_server.dto.ContestParticipantDto;
import com.contest.sports_programming_server.dto.CreateParticipantRequest;
import com.contest.sports_programming_server.dto.NewContestParticipantDto;
import com.contest.sports_programming_server.service.ContestParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/contest/{contest_id}/participants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name="Admin_ContestParticipants")
public class ContestParticipantController {

    private final ContestParticipantService contestParticipantService;

    @GetMapping("/")
    public List<ContestParticipantDto> findByContestId(@PathVariable("contest_id") UUID contestId) {
        return contestParticipantService.findByContestIdAsDto(contestId);
    }

    @PostMapping("/add/{participant_id}")
    public ResponseEntity<NewContestParticipantDto> joinContest(
            @PathVariable("contest_id") UUID contestId,
            @PathVariable("participant_id") UUID participantId
    ) {
        var dto = contestParticipantService.createContestParticipant(contestId, participantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/")
    public ResponseEntity<NewContestParticipantDto> createAndJoin(
            @PathVariable("contest_id") UUID contestId,
            @RequestBody CreateParticipantRequest request
    ) {
        var dto = contestParticipantService.createParticipantAndJoinContest(contestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/{contest_participant_id}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable("contest_id") UUID contestId,
            @PathVariable("contest_participant_id") UUID participantId
    ) {
        String password = contestParticipantService.setNewPassword(participantId);
        return ResponseEntity.status(HttpStatus.OK).body(password);
    }


}
