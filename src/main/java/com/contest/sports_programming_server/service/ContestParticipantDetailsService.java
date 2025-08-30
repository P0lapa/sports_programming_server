package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.model.ContestParticipantPrincipal;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ContestParticipantDetailsService implements UserDetailsService {

    ContestParticipantRepository contestParticipantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ContestParticipantEntity entity = contestParticipantRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return ContestParticipantPrincipal.builder()
                .contestParticipantId(entity.getId())
                .username(entity.getLogin())
                .password(entity.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }

}
