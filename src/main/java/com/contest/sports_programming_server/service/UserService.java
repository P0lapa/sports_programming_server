package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final ContestParticipantRepository contestParticipantRepository;

//    @Transactional(readOnly = true)
//    public LoginResponse login(String login, String password) {
//        log.debug("Attempting login for user: {}", login);
//        Optional<ContestParticipantEntity> optionalEntity = contestParticipantRepository.findByLogin(login);
//
//        if (optionalEntity.isEmpty()) {
//            log.warn("User with login {} not found", login);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
//        }
//
//        ContestParticipantEntity participant = optionalEntity.get();
//
//        // Проверка пароля (уже для хешированного пароля)
//        if (!passwordEncoder.matches(password, participant.getPassword())) {
//            log.warn("Invalid password for login: {}", login);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
//        }
//
//        log.info("Successful login for user: {}", login);
//        return new LoginResponse(participant.getLogin(), participant.getId());
//    }
    @Transactional(readOnly = true)
    public LoginResponse loginPlainText(String login, String password) {
        log.debug("Attempting login (plain-text) for user: {}", login);
        Optional<ContestParticipantEntity> optionalEntity = contestParticipantRepository.findByLogin(login);

        if (optionalEntity.isEmpty() || !optionalEntity.get().getPassword().equals(password)) {
            log.warn("Invalid login or password for login: {}", login);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }

        ContestParticipantEntity participant = optionalEntity.get();
        log.info("Successful login for user: {}", login);
        return new LoginResponse(participant.getLogin(), participant.getId());
    }
}
    /*====ТУТ СТАРЫЙ КОД С ПРОЕРКОЙ ЧТО СОРЕВНОВАНИЕ СЕГОДНЯ====НАДО ОБСУДИТЬ НАДО ЛИ ЭТО ЕЩЁ ВООБЩЕ*/

//    @Transactional(readOnly = true)
//    public LoginResponse findContest(String login, String password) {
//        Optional<ContestParticipantEntity> optionalEntity = contestParticipantRepository.findByLogin(login);
//        if (optionalEntity.isEmpty() || !optionalEntity.get().getPassword().equals(password)) {
//            log.warn("Invalid login or password for login: {}", login);
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
//        }
//
//        ContestParticipantEntity entity = optionalEntity.get();
//        ContestEntity contest = entity.getContest();
//
//        if (hasContestStarted(contest)) {
//            log.info("Contest {} has started for login: {}", contest.getId(), login);
//            return getLoginResponse(contest, true);
//        }
//
//        log.info("Contest {} has not started yet for login: {}", contest.getId(), login);
//        return getLoginResponse(contest, false);
//    }
//
//    private boolean hasContestStarted(ContestEntity contest) {
//        return contest.getStatus().equals(Status.STARTED) && contest.getStartDateTime().isBefore(LocalDateTime.now());
//    }
//
//    private LoginResponse getLoginResponse(ContestEntity contest, boolean loadTasks) {
//        List<TaskEntity> tasks = loadTasks ? taskRepository.findByContest_Id(contest.getId()) : Collections.emptyList();
//        return new LoginResponse(
//                contest.getId(),
//                contest.getName(),
//                contest.getDescription(),
//                taskMapper.toShortDtoList(tasks)
//        );
//    }
