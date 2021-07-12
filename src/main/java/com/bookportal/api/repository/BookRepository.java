package com.bookportal.api.repository;

import com.bookportal.api.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIdAndActiveTrue(Long id);

    Optional<Book> findByIdAndActiveTrueAndIsPublishedTrue(Long id);

    List<Book> findAllByActiveTrue();

    Page<Book> findByCategories_Id(Pageable pageable, Long id);

    Page<Book> findAllByActiveTrueAndIsPublishedTrue(Pageable pageable);

    Page<Book> findAllByActiveTrueAndNameIsContainingOrAuthors_NameIsContaining(Pageable pageable, String p1, String p2);
}
