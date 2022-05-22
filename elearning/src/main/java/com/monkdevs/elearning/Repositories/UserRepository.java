package com.monkdevs.elearning.Repositories;

import java.util.Optional;

import com.monkdevs.elearning.Models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    User findUserById(Long id);

    User getByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
