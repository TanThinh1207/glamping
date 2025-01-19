package com.group2.glamping.model.entity;

import com.group2.glamping.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_site")
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @OneToMany(mappedBy = "campSite")
    private List<Booking> bookingList;

    @OneToMany(mappedBy = "campSite")
    private List<Image> imageList;

    @OneToMany(mappedBy = "campSite")
    private List<Report> reportList;

}