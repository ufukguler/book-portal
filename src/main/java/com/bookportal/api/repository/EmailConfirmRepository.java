package com.bookportal.api.repository;

import com.bookportal.api.entity.EmailConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmRepository extends JpaRepository<EmailConfirm, Long> {
    Optional<EmailConfirm> findBySecretKeyAndActiveTrue(String secretKey);
}
