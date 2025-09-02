package com.contest.sports_programming_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer weight;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_task_contest"))
    private ContestEntity contest;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestEntity> tests = new ArrayList<>();
}
