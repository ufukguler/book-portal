package com.bookportal.api.repository;

import com.bookportal.api.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    Optional<Favourite> findByQuoteIdAndUserId(Long quoteId, Long userId);

    List<Favourite> findAllByQuoteIdAndActiveTrue(Long quoteId);

    List<Favourite> findAllByUserIdAndActiveTrue(Long userId);
}
