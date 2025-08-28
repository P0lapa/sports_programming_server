package com.contest.sports_programming_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "participants",
        indexes = @Index(name = "ux_participant_login", columnList = "login", unique = true)
)
public class ParticipantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 64, unique = true)
    private String login;
    private String email;
    private String password;
    private String fullName;
}
