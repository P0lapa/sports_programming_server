package com.contest.sports_programming_server.service;

import com.contest.sports_programming_server.dto.ContestParticipantShortDto;
import com.contest.sports_programming_server.dto.response.LoginResponse;
import com.contest.sports_programming_server.security.ContestParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(String login, String password) {

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication.getPrincipal() instanceof ContestParticipant principal) {
            String token = jwtService.generateToken(principal.getUsername());
            return LoginResponse.builder()
                    .user(new ContestParticipantShortDto(principal))
                    .token(token)
                    .build();
        } else if(authentication.getPrincipal() instanceof User user) {
            String token = jwtService.generateToken(user.getUsername());
            return LoginResponse.builder()
                    .user(new ContestParticipantShortDto(null, null))
                    .token(token)
                    .build();
        } else {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}
