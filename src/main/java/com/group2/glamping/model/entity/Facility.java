package com.group2.glamping.model.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "facility")
@JsonFilter("dynamicFilter")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String imageUrl;

    @Column(name = "status")
    private boolean status;

    @ManyToMany(mappedBy = "facilities")
    private List<CampType> campTypes;
}
