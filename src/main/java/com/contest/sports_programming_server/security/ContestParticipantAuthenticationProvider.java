package com.contest.sports_programming_server.security;
import com.contest.sports_programming_server.config.SecureProperties;
import com.contest.sports_programming_server.entity.ContestParticipantEntity;
import com.contest.sports_programming_server.repository.ContestParticipantRepository;
import com.contest.sports_programming_server.service.ContestParticipantDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ContestLogger")
public class ContestParticipantAuthenticationProvider implements AuthenticationProvider {

    private final ContestParticipantRepository contestParticipantRepository;
    private final ContestParticipantDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final SecureProperties secureProperties;

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return UsernamePasswordAuthenticationToken.authenticated(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}
