package com.contest.sports_programming_server.entity;

import com.contest.sports_programming_server.dto.ContestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
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
@Table(name = "contests")
public class ContestEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    private ContestStatus contestStatus;

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContestParticipantEntity> participants = new ArrayList<>();

    @AssertTrue(message = "startDateTime must be before endDateTime")
    private boolean isValidDateRange() {
        if (startDateTime == null || endDateTime == null) {
            return true; // Null checks are handled by other validations
        }
        return startDateTime.isBefore(endDateTime);
    }
}
