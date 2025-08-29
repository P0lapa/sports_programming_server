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
@Table(name = "tests")
public class TestEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_test_task"))
    private TaskEntity task;

    @Column(nullable = false)
    private Integer testNumber;

    @Column(nullable = false)
    private Boolean isPublic;

    @Lob
    @Column(nullable = false)
    private String inputData;

    @Lob
    @Column(nullable = false)
    private String expectedOutput;

}
