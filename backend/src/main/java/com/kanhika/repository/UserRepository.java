package com.kanhika.repository;

import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Return the User object based on the username
    // Case insensitive and user must be active
    Optional<User> findByUsernameIgnoreCaseAndDisabledFalse(String username);

    // Check if the username is already taken
    // Username are locked forever even for disabled users
    boolean existsByUsernameIgnoreCase(String username);

    // Check if the email exists
    // Case insensitive and user must be active or banned
    // Emails are freed if the user is disabled, but locked if banned
    @Query("""
        SELECT COUNT(u) > 0
        FROM User u
        WHERE u.email = :email
        AND (u.disabled = false OR u.banned = true)
    """)
    boolean existsByEmailUsedOrBanned(@Param("email") String email);
}
