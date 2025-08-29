package com.contest.sports_programming_server.entity;

import com.contest.sports_programming_server.dto.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attempts",
        indexes = {
                @Index(name = "ix_attempt_participant_task", columnList = "contest_participant_id, task_id")
        })
public class AttemptEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_contest_participant"))
    private ContestParticipantEntity participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attempt_task"))
    private TaskEntity task;

    @Column(nullable = false)
    private Integer attemptNumber;

    @Column(nullable = false)
    private Boolean success;

    @Column(nullable = false)
    private LocalDateTime submissionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Language language;

    @Lob
    @Column(nullable = false)
    private String solution;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptTestResultEntity> testResults = new ArrayList<>();


    @PrePersist
    public void prePersist() {
        if (submissionTime == null) {
            submissionTime = LocalDateTime.now();
        }
    }
}
