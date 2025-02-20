package com.group2.glamping.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fcm_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

}
