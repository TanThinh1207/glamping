package com.group2.glamping.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.group2.glamping.model.enums.CampSiteStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "camp_site")
@Builder(toBuilder = true)
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CampSiteStatus status;

    @Column(name = "message", columnDefinition = "TEXT DEFAULT 'No message provided'")
    private String message = "No message provided";

    @Column(name = "deposit_rate", columnDefinition = "DOUBLE DEFAULT 0.0", nullable = false)
    private Double depositRate;

    @Column(name = "description", columnDefinition = "TEXT DEFAULT 'No description provided'")
    private String description = "No description provided";

    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "campSite")
    private List<Booking> bookingList;

    @OneToMany(mappedBy = "campSite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList;


    @OneToMany(mappedBy = "campSite")
    private List<Report> reportList;


    @ManyToMany
    @JoinTable(
            name = "camp_site_utility",
            joinColumns = @JoinColumn(name = "id_camp_site", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_utility", referencedColumnName = "id")
    )
    private List<Utility> utilities;

    @OneToMany(mappedBy = "campSite", cascade = CascadeType.ALL)
    private List<CampType> campTypes;


    @ManyToMany
    @JoinTable(
            name = "camp_site_place_type",
            joinColumns = @JoinColumn(name = "id_camp_site", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_place_type", referencedColumnName = "id")
    )
    private List<PlaceType> placeTypes;

    @OneToMany(mappedBy = "campSite", cascade = CascadeType.ALL)
    private List<Selection> selections;

    @Override
    public String toString() {
        return "CampSite(id=" + this.id + ", name=" + this.name + ")";
    }

}