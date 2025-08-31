// src/main/java/com/contest/sports_programming_server/service/AdminService.java
package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.dto.request.CreateTaskRequest;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ContestRepository contestRepo;
    private final TaskRepository taskRepo;
    private final ParticipantRepository participantRepo;
    private final ContestParticipantRepository contestParticipantRepo;

    private final PasswordEncoder passwordEncoder;

    /* ====== турниры ====== */

    @Transactional
    public ContestEntity createTournament(CreateContestRequest req) {
        var entity = ContestEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(req.getStatus())
                .build();
        return contestRepo.save(entity);
    }

    /* ====== задачи ====== */

    @Transactional
    public TaskEntity createTask(CreateTaskRequest req) {
        var contest = contestRepo.findById(req.getContestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contestId not found"));
        var task = TaskEntity.builder()
                .name(req.getName())
                .description(req.getDescription())
                .contest(contest)
                .build();
        return taskRepo.save(task);
    }

    @Transactional
    public List<TaskEntity> createTasks(List<CreateTaskRequest> requests) {
        return requests.stream().map(this::createTask).toList();
    }

    /* ====== участники ====== */

    @Transactional
    public void createParticipant() {

    }

    @Transactional
    public CreateParticipantResponse createParticipantAndJoinContest(CreateParticipantRequest req) {

        var p = ParticipantEntity.builder()
                .fullName(req.getFullName())
                .email(req.getContact())
                .build();
        participantRepo.save(p);

        ContestParticipantDto contestParticipant =  joinContest(req.getContestId() , p.getId());

        return new CreateParticipantResponse(p.getId(), req.getContestId(), contestParticipant.login(), contestParticipant.password());
    }

    @Transactional
    public ContestParticipantDto joinContest(UUID contestId, UUID participantId) {

        var contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contestId not found"));
        var participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "participantId not found"));
        String login = generateLogin();
        while (contestParticipantRepo.existsByLogin(login)) login = generateLogin();
        String password = generatePassword();

        var link = ContestParticipantEntity.builder()
                .contest(contest)
                .participant(participant)
                .login(login)
                .password(passwordEncoder.encode(password))
                .build();
        ContestParticipantEntity entity = contestParticipantRepo.save(link);

        return toDTO(entity, password);
    }


    /* ====== генерация паролей и логинов ====== */

    private static final SecureRandom RNG = new SecureRandom();

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

    /* ======= Entity -> DTO ========== */

    private ContestParticipantDto toDTO(ContestParticipantEntity entity, String password) {
        return new ContestParticipantDto(
                entity.getId(),
                entity.getLogin(),
                password,
                entity.getContest().getId(),
                entity.getParticipant().getId()
        );
    }
}
