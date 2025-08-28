// src/main/java/com/contest/sports_programming_server/service/AdminService.java
package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.*;
import com.contest.sports_programming_server.entity.*;
import com.contest.sports_programming_server.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public CreateParticipantResponse createParticipantAndJoinContest(CreateParticipantRequest req) {
        var contest = contestRepo.findById(req.getContestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contestId not found"));

        String login = generateLogin();
        while (participantRepo.existsByLogin(login)) login = generateLogin();
        String password = generatePassword();

        var p = ParticipantEntity.builder()
                .fullName(req.getFullName())
                .email(req.getContact())
                .login(login)
                .email(req.getContact())   // если контакт = email — заполнится
                .password(password)        // хешируй позже
                .build();
        participantRepo.save(p);

        if (!contestParticipantRepo.existsByContest_IdAndParticipant_Id(contest.getId(), p.getId())) {
            var link = ContestParticipantEntity.builder()
                    .contest(contest)
                    .participant(p)
                    .build();
            contestParticipantRepo.save(link);
        }

        return new CreateParticipantResponse(p.getId(), contest.getId(), login, password);
    }

    @Transactional
    public void joinExistingParticipant(UUID contestId, UUID participantId) {
        var contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contestId not found"));
        var participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "participantId not found"));

        if (contestParticipantRepo.existsByContest_IdAndParticipant_Id(contestId, participantId)) return;

        var link = ContestParticipantEntity.builder()
                .contest(contest)
                .participant(participant)
                .build();
        contestParticipantRepo.save(link);
    }

//    @Transactional(readOnly = true)
    public List<TaskListItemDto> listTasks() {
        return taskRepo.findAllAsDto();
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
}
