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
@Entity(name = "selection")
public class Selection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "image")
    private String imageUrl;

    @Column(name = "status")
    private boolean status;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "selection", cascade = CascadeType.ALL)
    private List<BookingSelection> bookingSelectionList;

    @ManyToMany(mappedBy = "selections")
    private List<CampSite> campSiteList;
}
