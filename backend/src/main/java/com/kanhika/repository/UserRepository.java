package com.kanhika.repository;

import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Return the User object based on the username
    // Case insensitive and user must be active
    Optional<User> findByUsernameIgnoreCaseAndDisabledFalse(String username);

    // Check if the username is already taken
    // Username are locked forever even for disabled users
    boolean existsByUsernameIgnoreCase(String username);

    // Check if the username exists
    // Case insensitive and user must be active
    boolean existsByUsernameIgnoreCaseAndDisabledFalse(String username);

    // Check if the email exists
    // Case insensitive and user must be active
    // Emails are not locked forever, they are freed if the user is disabled
    boolean existsByEmailIgnoreCaseAndDisabledFalse(String email);
}
