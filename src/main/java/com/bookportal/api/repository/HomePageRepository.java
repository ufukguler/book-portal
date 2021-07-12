package com.bookportal.api.repository;

import com.bookportal.api.entity.HomePage;
import com.bookportal.api.model.enums.HomePageEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomePageRepository extends JpaRepository<HomePage, Long> {
    List<HomePage> findAllByType(HomePageEnum homePageEnum);

    Optional<HomePage> findByType(HomePageEnum homePageEnum);

    Optional<HomePage> findByTypeAndBook_Id(HomePageEnum homePageEnum, Long id);
}
