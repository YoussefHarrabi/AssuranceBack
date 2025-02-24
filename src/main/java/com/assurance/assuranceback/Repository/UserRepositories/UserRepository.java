package com.assurance.assuranceback.Repository.UserRepositories;

import com.assurance.assuranceback.Entity.UserEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


    boolean existsByEmail(String email);

    Optional<User> findByNumberOfIdentity(String numberOfIdentity);
}