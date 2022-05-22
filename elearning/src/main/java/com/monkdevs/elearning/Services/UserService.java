package com.monkdevs.elearning.Services;

import java.util.List;
import java.util.Optional;

import com.monkdevs.elearning.Models.User;

public interface UserService {
    Optional<User> findByUsername(String username);

    User findUserById(Long id);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
