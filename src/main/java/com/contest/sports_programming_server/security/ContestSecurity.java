package com.contest.sports_programming_server.security;

import com.contest.sports_programming_server.dto.ContestStatus;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.exception.ContestNotRunningException;
import com.contest.sports_programming_server.exception.ParticipationFinishedException;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class ContestSecurity {

    private final ContestParticipantRepository participantRepository;


    public boolean hasActiveContest(String username) {

        ContestParticipantEntity participant = participantRepository.findByLoginWithContest(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (participant.getFinishedAt() != null) {
            log.warn("Participant {} tries to join contest after finishing", participant.getId());
            throw new ParticipationFinishedException();
        }

         if (participant.getContest().getContestStatus() != ContestStatus.STARTED) {
             log.warn("Participant {} tries to join contest before starting or after finishing", participant.getId());
            throw new ContestNotRunningException();
        }

        return true;
    }

}
