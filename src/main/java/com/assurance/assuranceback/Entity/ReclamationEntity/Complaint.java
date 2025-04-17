package com.assurance.assuranceback.Entity.ReclamationEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.assurance.assuranceback.Enum.ComplaintStatus;
import com.assurance.assuranceback.Enum.ComplaintType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING) // Stocke l'Enum sous forme de texte en DB
    @Column(nullable = false)
    private ComplaintStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastModifiedDate;

    @Enumerated(EnumType.STRING) // Stocke l'Enum sous forme de texte en DB
    @Column(nullable = false)
    private ComplaintType type;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToOne(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Response response;

    @OneToOne(mappedBy = "complaint", cascade = CascadeType.ALL)
    private Review review;



    @PrePersist
    protected void onCreate() {

        this.creationDate = new Date(); // Assigne la date actuelle
        this.lastModifiedDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = new Date();
    }
}
