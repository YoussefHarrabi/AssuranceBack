package com.assurance.assuranceback.Entity.ReclamationEntity;

import com.assurance.assuranceback.Entity.UserEntity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    @Column(nullable = false)
    private LocalDateTime date; // Change to LocalDateTime for better date handling


    @OneToOne
    @JoinColumn(name = "complaint_id")
    @JsonBackReference
    private Complaint complaint;



    @ManyToOne  // Cascading persist and merge operations
    @JoinColumn(name = "advisor_id")
    private User advisor;
}
