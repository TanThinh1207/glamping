package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "camp_type")
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

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "image")
    private String image;

    @Column(name = "status")
    private boolean status;

    @ManyToMany
    @JoinTable(
            name = "camp_type_facility",
            joinColumns = @JoinColumn(name = "id_camp_type", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_facility", referencedColumnName = "id")
    )
    private List<Facility> facilities;

    @OneToMany(mappedBy = "campType")
    private List<BookingDetail> bookingDetailList;

    @OneToMany(mappedBy = "campType")
    private List<Camp> campList;

    @OneToMany(mappedBy = "campType")
    private List<CustomPrice> customPriceList;
}
