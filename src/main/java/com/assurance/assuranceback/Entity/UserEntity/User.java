package com.assurance.assuranceback.Entity.UserEntity;


import com.assurance.assuranceback.Entity.ReclamationEntity.Complaint;
import com.assurance.assuranceback.Entity.ReclamationEntity.Response;
import com.assurance.assuranceback.Entity.ReclamationEntity.Review;
import com.assurance.assuranceback.Enum.IdentityType;
import com.assurance.assuranceback.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityType identityType;

    @Column(unique = true, nullable = false)
    private String numberOfIdentity;

    @Column(nullable = false)
    private String phoneNumber;

    private String address;
    // Add the password field
    @Column(nullable = false)
    private String password;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Complaint> complaints;


    @JsonIgnore
    @OneToMany(mappedBy = "advisor", cascade = CascadeType.ALL)
    private List<Response> responses;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Review> reviews;

    public boolean isAdvisor() {
        return roles.contains(Role.ADVISOR);
    }
    public boolean isClient() {
        return roles.contains(Role.CLIENT);
    }
}