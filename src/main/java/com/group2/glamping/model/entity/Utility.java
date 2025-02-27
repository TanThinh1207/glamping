package com.group2.glamping.model.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "utility")
@JsonFilter("dynamicFilter")
public class Utility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String imageUrl;

    @Column(name = "status")
    private boolean status;

    @ManyToMany(mappedBy = "utilities")
    private List<CampSite> campSites;
}
