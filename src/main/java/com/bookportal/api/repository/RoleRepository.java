package com.bookportal.api.repository;

import com.bookportal.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String role);

    boolean existsByName(String name);
}
