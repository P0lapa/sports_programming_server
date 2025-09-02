package com.contest.sports_programming_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

    @Column(length = 64, unique = true)
    private String login;

    @Column
    private String password;

    @Column(precision = 5, scale = 1)
    private BigDecimal result;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public void setResult(double value) {
        this.result = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }

}
