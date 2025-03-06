package com.assurance.assuranceback.Repository.UserRepositories;

import com.assurance.assuranceback.Entity.UserEntity.PasswordResetToken;
import com.assurance.assuranceback.Entity.UserEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
}