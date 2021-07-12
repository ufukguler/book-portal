package com.bookportal.api.repository;

import com.bookportal.api.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByUserId(Long userId);

    Optional<Vote> findByUserIdAndBookId(Long userId, Long bookId);
}
