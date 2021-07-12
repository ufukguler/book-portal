package com.bookportal.api.repository;

import com.bookportal.api.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findBySecretKeyAndActiveTrue(String secretKey);
}
