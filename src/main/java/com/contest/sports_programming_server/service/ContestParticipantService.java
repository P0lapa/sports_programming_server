package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.ContestParticipantDto;
import com.contest.sports_programming_server.dto.CreateParticipantRequest;
import com.contest.sports_programming_server.dto.NewContestParticipantDto;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.mapper.ContestParticipantMapper;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContestParticipantService {

    private final ContestParticipantRepository repository;
    private final ContestParticipantMapper mapper;

    private final ContestService contestService;
    private final ParticipantService participantService;

    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom RNG = new SecureRandom();

    public List<ContestParticipantEntity> findByContestId(UUID contestId) {
        return repository.findByContest_Id(contestId);
    }

    public List<ContestParticipantDto> findByContestIdAsDto(UUID contestId) {
        return mapper.toDTOList(findByContestId(contestId));
    }

    @Transactional
    public NewContestParticipantDto createParticipantAndJoinContest(UUID contestId, CreateParticipantRequest request) {
        var participant = participantService.create(request);
        return createContestParticipant(contestId, participant.id());
    }

    @Transactional
    public NewContestParticipantDto createContestParticipant(UUID contestId, UUID participantId) {
        var contest = contestService.getContestOrThrow(contestId);
        var participant = participantService.getParticipantOrThrow(participantId);
        String login = generateLogin();
        while (repository.existsByLogin(login)) login = generateLogin();
        String password = generatePassword();

        var entity = ContestParticipantEntity.builder()
                .contest(contest)
                .participant(participant)
                .login(login)
                .password(passwordEncoder.encode(password))
                .build();
        entity = repository.save(entity);

        return mapper.toDTO(entity, password);
    }

    @Transactional
    public void finishContestForParticipant(UUID contestParticipantId) {
        ContestParticipantEntity entity = repository.findById(contestParticipantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        entity.setFinishedAt(LocalDateTime.now());
        repository.save(entity);
    }

    private static String generateLogin() {
        return "u-" + HexFormat.of().withUpperCase().formatHex(randomBytes(4));
    }

    private static String generatePassword() {
        final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        var sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
        return sb.toString();
    }

    private static byte[] randomBytes(int n) {
        var b = new byte[n];
        RNG.nextBytes(b);
        return b;
    }

}
