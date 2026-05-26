package com.authify.authenticationsystem.repository;

import com.authify.authenticationsystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByVerifyOtp(String verifyOtp);

    Optional<UserEntity> findByResetOtp(String resetOtp);
}
