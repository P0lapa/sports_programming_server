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
        name = "participants"
)
public class ParticipantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;
    private String fullName;
}
