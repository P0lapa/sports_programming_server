package com.contest.sports_programming_server.controller.admin;

import com.contest.sports_programming_server.dto.CreateParticipantRequest;
import com.contest.sports_programming_server.dto.ParticipantDto;
import com.contest.sports_programming_server.service.ParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/participants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name="Admin_Participants")
public class ParticipantController {

    private final ParticipantService participantService;

    @GetMapping("/")
    public List<ParticipantDto> listParticipants() {
        return participantService.findAllAsDto();
    }

    @PostMapping("/")
    public ResponseEntity<ParticipantDto> createParticipant(@RequestBody CreateParticipantRequest req) {
        var dto = participantService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

}
