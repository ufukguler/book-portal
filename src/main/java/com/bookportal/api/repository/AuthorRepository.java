package com.bookportal.api.repository;

import com.bookportal.api.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findAllByActiveTrue();

    Optional<Author> findByIdAndActiveTrue(Long id);

    Page<Author> findAllByActiveTrue(Pageable pageable);
}
