package com.assurance.assuranceback.Repository.UserRepositories;

import com.assurance.assuranceback.Entity.UserEntity.MfaInfo;
import com.assurance.assuranceback.Entity.UserEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MfaInfoRepository extends JpaRepository<MfaInfo, Long> {
    Optional<MfaInfo> findByUser(User user);
}