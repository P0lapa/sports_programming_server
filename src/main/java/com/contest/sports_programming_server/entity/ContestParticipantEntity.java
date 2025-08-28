package com.contest.sports_programming_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "contest_participants",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_contest_participant",
                columnNames = {"contest_id","participant_id"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContestParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cp_contest"))
    private ContestEntity contest;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cp_participant"))
    private ParticipantEntity participant;
}
