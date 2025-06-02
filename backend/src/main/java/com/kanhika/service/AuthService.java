package com.kanhika.service;

import com.kanhika.dto.auth.AuthDTO;
import com.kanhika.dto.auth.LoginDTO;
import com.kanhika.dto.auth.RegisterDTO;
import com.kanhika.exception.ConflictException;
import com.kanhika.exception.InvalidFormatException;
import com.kanhika.model.User;
import com.kanhika.repository.UserRepository;
import com.kanhika.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthDTO register(RegisterDTO request) {
        // Check username format, must be only letters, numbers, '-' and '_'
        if (!request.username().matches("^[a-zA-Z0-9-_]+$")) {
            throw new InvalidFormatException("Username can only contain letters, numbers, '-' and '_'.");
        }

        // Check email format, + addresses not allowed
        if (!request.email().matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidFormatException("Invalid email format.");
        }

        // Check password format, must be at least 8 characters long
        if (request.password().length() < 8) {
            throw new InvalidFormatException("Password must be at least 8 characters long.");
        }

        // Check username uniqueness
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ConflictException("This username is already taken.");
        }

        // Check email uniqueness
        if (userRepository.existsByEmailUsedOrBanned(request.email())) {
            throw new ConflictException("There is already an account linked with this email address.");
        }

        // Create the user object
        User user = new User();

        // Set the user properties
        user.setUsername(request.username());
        user.setBio("");
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setExp(0);
        user.setFlame(0);
        user.setRole("USER");
        user.setDisabled(false);

        // Insert the user in database
        userRepository.save(user);

        // Return the JWT token
        String token = jwtService.generateToken(user.getUsername());
        return new AuthDTO(token);
    }

    public AuthDTO login(LoginDTO request) {
        // Authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), request.password()
                )
        );

        // Get the user
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        // Return the JWT token
        String token = jwtService.generateToken(user.getUsername());
        return new AuthDTO(token);
    }
}
