package com.contest.sports_programming_server.config;

import com.contest.sports_programming_server.security.ContestParticipantAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@RequiredArgsConstructor
public class AuthenticationManagerConfig {

    private final ContestParticipantAuthenticationProvider contestParticipantAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(final ObjectPostProcessor<Object> objectPostProcessor)
            throws Exception{
        var auth = new AuthenticationManagerBuilder(objectPostProcessor);
        auth.authenticationProvider(contestParticipantAuthenticationProvider);
        return auth.build();

    }

}
