package com.contest.sports_programming_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "contests")
public class ContestEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String status;

}
