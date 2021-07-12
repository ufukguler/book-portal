package com.bookportal.api.repository;

import com.bookportal.api.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByActiveTrue();

    Page<Comment> findAllByActiveTrue(Pageable pageable);

    Optional<Comment> findByBook_IdAndUser_Id(Long bookId, Long userId);

    Page<Comment> findByBook_Id(Pageable pageable, Long bookId);
}
