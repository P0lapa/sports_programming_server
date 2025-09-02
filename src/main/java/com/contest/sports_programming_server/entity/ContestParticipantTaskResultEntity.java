package com.contest.sports_programming_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(
        name = "contest_participant_task_results",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_cp_task",
                columnNames = {"contest_participant_id", "task_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestParticipantTaskResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cptr_contest_participant"))
    private ContestParticipantEntity participant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cptr_task"))
    private TaskEntity task;

    @Column
    private Integer attemptsCount;

    @Column
    private Integer passedTestsCount;

    @Column(precision = 5, scale = 1)
    private BigDecimal score;

    public void setScore(double value) {
        this.score = BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }

    public double getScoreAsDouble() {
        return score != null ? score.doubleValue() : 0.0;
    }

}
