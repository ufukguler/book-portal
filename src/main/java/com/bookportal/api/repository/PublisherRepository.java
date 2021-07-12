package com.bookportal.api.repository;

import com.bookportal.api.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByNameAndActiveTrue(String name);

    Optional<Publisher> findByIdAndActiveTrue(Long id);

    List<Publisher> findAllByActiveTrue();

    Page<Publisher> findAllByActiveTrue(Pageable pageable);
}
