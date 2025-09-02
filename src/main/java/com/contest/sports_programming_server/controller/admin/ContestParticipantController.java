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
@RequestMapping("/api/admin/contest/{id}/participants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name="Admin_ContestParticipants")
public class ContestParticipantController {

    private final ContestParticipantService contestParticipantService;

    @GetMapping("/")
    public List<ContestParticipantDto> findByContestId(@PathVariable("id") UUID contestId) {
        return contestParticipantService.findByContestIdAsDto(contestId);
    }

    @PostMapping("/add/{participantId}")
    public ResponseEntity<NewContestParticipantDto> joinContest(
            @PathVariable("id") UUID contestId,
            @PathVariable UUID participantId
    ) {
        var dto = contestParticipantService.createContestParticipant(contestId, participantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/")
    public ResponseEntity<NewContestParticipantDto> createAndJoin(
            @PathVariable("id") UUID contestId,
            @RequestBody CreateParticipantRequest request
    ) {
        var dto = contestParticipantService.createParticipantAndJoinContest(contestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/{contestParticipantId}/reset-password")
    public ResponseEntity<String> resetPassword(
            @PathVariable("id") UUID contestId,
            @PathVariable("contestParticipantId") UUID participantId
    ) {
        String password = contestParticipantService.setNewPassword(participantId);
        return ResponseEntity.status(HttpStatus.OK).body(password);
    }


}
