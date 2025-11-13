package com.realestate.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estate_characteristics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstateCharacteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estate_id", nullable = false)
    private Estate estate;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;
}
