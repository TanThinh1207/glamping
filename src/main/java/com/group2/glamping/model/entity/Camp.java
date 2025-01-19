package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp")
public class Camp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "status")
    private boolean status;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "price")
    private double price;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @Column(name = "view")
    private String view;

    @Column(name = "id_camp_type")
    private int campTypeId;

    @OneToMany(mappedBy = "camp")
    private List<CampFacility> campFacilities;
}
