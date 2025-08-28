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
        name = "solutions",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_solution_participant_task",
                columnNames = {"participant_id", "task_id"}
        )
)
public class SolutionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    // FK → participants.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_solution_participant"))
    private ParticipantEntity participant;

    // FK → tasks.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_solution_task"))
    private TaskEntity task;

}
