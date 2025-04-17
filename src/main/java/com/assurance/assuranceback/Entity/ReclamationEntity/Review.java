package com.assurance.assuranceback.Entity.ReclamationEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @Column(nullable = false)
    private int rating; // Note de 1 à 5

    // Catégories sur lesquelles porte l'avis
    @ElementCollection
    @CollectionTable(name = "review_categories", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "category")
    private Set<String> categories = new HashSet<>();

    // Sentiments détectés dans l'avis
    private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL

    // Photos jointes à l'avis
    @ElementCollection
    @CollectionTable(name = "review_attachments", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "attachment_url")
    private Set<String> attachments = new HashSet<>();

    // Nombre de votes "utile" sur cet avis
    private int helpfulVotes = 0;

    // Visibilité de l'avis (certains avis peuvent être privés)
    private boolean isPublic = true;

    // État de modération
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    // Réponse officielle de l'entreprise
    @Column(length = 1000)
    private String companyResponse;

    // Horodatages
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relations
    @OneToOne
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // Utilisateurs qui ont marqué cet avis comme utile
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "review_helpful_votes",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> votedByUsers = new HashSet<>();
}