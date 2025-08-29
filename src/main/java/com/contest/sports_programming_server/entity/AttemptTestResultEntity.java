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
@Table(name = "attempt_test_results")
public class AttemptTestResultEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_test_result_attempt"))
    private AttemptEntity attempt;

    private Boolean passed;
    private String reason;
    private Double timeSeconds;
    private Long memoryBytes;

}
