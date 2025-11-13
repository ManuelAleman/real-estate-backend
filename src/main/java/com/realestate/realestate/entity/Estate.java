package com.realestate.realestate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.enums.EstateType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "estates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstateType type = EstateType.SALE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstateStatus status = EstateStatus.WAITING_FOR_APPROVAL;

    @OneToMany(mappedBy = "estate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EstateImage> images = new ArrayList<>();


    @OneToMany(mappedBy = "estate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EstateCharacteristic> characteristics = new ArrayList<>();

    @OneToMany(mappedBy = "estate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Meeting> meetings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
