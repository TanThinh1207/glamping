package com.group2.glamping.model.entity;

import com.group2.glamping.model.enums.CampStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "camp")
public class Camp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CampStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "id_camp_type")
    private CampType campType;

    @OneToMany(mappedBy = "camp")
    private List<BookingDetail> bookingDetailList;

    @Override
    public String toString() {
        return "CampSite(id=" + this.id + ", name=" + this.name + ")";
    }


}
