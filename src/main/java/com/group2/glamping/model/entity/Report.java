package com.group2.glamping.model.entity;

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

    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "message")
    private String message;

    @Column(name = "report_type")
    private String reportType;
}
