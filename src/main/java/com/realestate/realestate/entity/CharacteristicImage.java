package com.realestate.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "characteristic_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "characteristic_id", nullable = false)
    private EstateCharacteristic characteristic;

    @Column(nullable = false)
    private String s3url;
}
