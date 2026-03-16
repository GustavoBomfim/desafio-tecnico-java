package br.com.shipay.ms_bkd_user.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "claims")
public class ClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "decription", nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active = true;


    public ClaimEntity(String description, boolean active) {
        this.description = description;
        this.active = active;
    }
}
