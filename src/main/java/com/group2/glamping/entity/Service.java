package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "status")
    private boolean status;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "service",cascade = CascadeType.ALL)
    private List<Booking_Detail_Service> Booking_Detail_Service_List = new ArrayList<>();
}
