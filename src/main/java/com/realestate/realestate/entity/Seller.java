package com.realestate.realestate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.realestate.realestate.enums.SellerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "sellers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"user", "estates", "meetings"})
@ToString(exclude = {"user", "estates", "meetings"})
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = true, length = 100)
    private String companyName;

    @Column(nullable = true, length = 50)
    private String licenseNumber;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerStatus status = SellerStatus.PENDING;

    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    @Builder.Default
    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(columnDefinition = "jsonb")
    private String verificationDocuments;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Estate> estates = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Meeting> meetings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
