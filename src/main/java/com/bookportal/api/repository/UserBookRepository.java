package com.bookportal.api.repository;

import com.bookportal.api.entity.UserBook;
import com.bookportal.api.model.enums.UserBookEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    Optional<UserBook> findByUser_IdAndBook_IdAndType(Long userId, Long bookId, UserBookEnum type);
}
