package com.contest.sports_programming_server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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
//    private LocalDate startDate; //возможно поменять String на LocalDate и LocalTime но тогда делаем валидацию на фронте и на беке
//    private LocalTime startTime;
//    private LocalDate endDate;
//    private LocalTime endTime;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String status;

}
