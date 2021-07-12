package com.bookportal.api.repository;

import com.bookportal.api.entity.Top20;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Top20Repository extends JpaRepository<Top20, Long> {
}
