package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.config.SecureProperties;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import com.contest.sports_programming_server.security.ContestParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class ContestParticipantDetailsService implements UserDetailsService {

    private final ContestParticipantRepository contestParticipantRepository;
    private final SecureProperties secureProperties;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(username.equals(secureProperties.getUsername())){
            return User.builder()
                    .username(username)
                    .password(secureProperties.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        } else {
            ContestParticipantEntity entity = contestParticipantRepository.findByLogin(username)
                    .orElseThrow(() -> new UsernameNotFoundException(username));
            return new ContestParticipant(
                    entity.getLogin(),
                    entity.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")),
                    entity.getContest().getId(),
                    entity.getId()
            );
        }

    }

}
