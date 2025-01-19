package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_camp_site")
    private int campSiteId;

    @Column(name = "id_user")
    private int userId;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "message")
    private String message;

    @Column(name = "report_type")
    private String reportType;
}
