package com.bookportal.api.repository;

import com.bookportal.api.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Optional<Quote> findByQuote(String quote);

    Page<Quote> findAllByActiveTrue(Pageable pageable);

    List<Quote> findAllByActiveTrue();

    Optional<Quote> findQuoteByIdAndActiveTrue(Long id);

    @Query(value = "SELECT * FROM QUOTE WHERE ACTIVE=1 ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Quote> findRandomQuoteByActiveTrue();

}
