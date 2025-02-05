package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
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

    @Column(name = "holiday_rate")
    private double holidayRate;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "id_camp_site")
    private CampSite campSite;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "status")
    private boolean status = true;

    @OneToMany(mappedBy = "campType")
    private List<CampTypeFacility> campTypeFacilityList;

    @OneToMany(mappedBy = "campType")
    private List<BookingDetail> bookingDetailList;
}
