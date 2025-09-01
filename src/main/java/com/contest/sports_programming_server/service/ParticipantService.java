package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.NewContestParticipantDto;
import com.contest.sports_programming_server.dto.CreateParticipantRequest;
import com.contest.sports_programming_server.dto.ParticipantDto;
import com.contest.sports_programming_server.entity.ParticipantEntity;
import com.contest.sports_programming_server.mapper.ParticipantMapper;
import com.contest.sports_programming_server.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    public List<ParticipantEntity> findAll() {
        return participantRepository.findAll();
    }

    public List<ParticipantDto> findAllAsDto() {
        return participantMapper.toDto(findAll());
    }

    @Transactional
    public ParticipantDto create(CreateParticipantRequest request) {
        ParticipantEntity entity = ParticipantEntity.builder()
                .email(request.getContact())
                .fullName(request.getFullName())
                .build();
        return participantMapper.toDto(participantRepository.save(entity));
    }

    public ParticipantEntity getParticipantOrThrow(UUID id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found with id: " + id));
    }
}
