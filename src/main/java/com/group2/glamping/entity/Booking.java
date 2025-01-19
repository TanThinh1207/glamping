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
@Entity(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_user")
    private int id_user;

    @Column(name = "id_camp_site")
    private int id_camp_site;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private double total_amount;

    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL)
    private List<Booking_Detail_Service> Booking_Detail_Service_List = new ArrayList<>();

}
