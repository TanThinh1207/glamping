package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "place_type")
public class PlaceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private boolean status;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "placeType")
    private List<CampSitePlaceType> campSitePlaceTypes;
}
