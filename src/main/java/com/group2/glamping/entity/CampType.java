package com.group2.glamping.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_type")
public class CampType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "price")
    private double price;

    @Column(name = "weekend_rate")
    private double weekendRate;

    @Column(name = "holiday_rate")
    private double holidayRate;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @Column(name = "id_camp_site")
    private int campSiteId;

    @Column(name = "quantity")
    private int quantity;
}
