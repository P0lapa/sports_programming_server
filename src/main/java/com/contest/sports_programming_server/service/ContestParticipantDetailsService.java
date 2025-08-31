package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import lombok.RequiredArgsConstructor;
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
public class ContestParticipantDetailsService implements UserDetailsService {

    private final ContestParticipantRepository contestParticipantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ContestParticipantEntity entity = contestParticipantRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(entity.getLogin(), entity.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
