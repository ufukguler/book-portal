package com.bookportal.api.repository;

import com.bookportal.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndActiveTrue(Long id);

    Optional<User> findByMail(String mail);

    Optional<User> findByMailAndActiveTrue(String mail);

    Optional<User> findByMailAndActiveFalse(String mail);

    Optional<User> findByFacebookId(String socialId);

    Optional<User> findByGoogleId(String socialId);

    Optional<User> findByGoogleIdOrFacebookId(String googleId, String facebookId);

    @Modifying
    @Query(value = "update User u set u.active = false where u.id=:userId")
    void setToInactive(@Param("userId") Long userId);

    @Modifying
    @Query(value = "update User u set u.active = true where u.id=:userId")
    void setToActive(@Param("userId") Long userId);
}