package com.contest.sports_programming_server.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class ContestParticipant extends User {

    private final UUID contestId;
    private final UUID participantId;

    public ContestParticipant(String username, String password,
                                     Collection<? extends GrantedAuthority> authorities,
                                     UUID contestId, UUID participantId) {
        super(username, password, authorities);
        this.contestId = contestId;
        this.participantId = participantId;
    }

}
