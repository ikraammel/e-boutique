package com.ecommerce.demo.repositories;

import com.ecommerce.demo.models.Role;
import com.ecommerce.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Long countByRole(Role role);
}
