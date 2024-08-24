package com.asusoftware.user_api.repository;

import com.asusoftware.user_api.model.RegularUser;
import com.asusoftware.user_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<RegularUser, UUID> {
    Optional<RegularUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
