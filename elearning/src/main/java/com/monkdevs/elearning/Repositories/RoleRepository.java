package com.monkdevs.elearning.Repositories;

import java.util.Optional;

import com.monkdevs.elearning.Models.Role;
import com.monkdevs.elearning.Models.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
