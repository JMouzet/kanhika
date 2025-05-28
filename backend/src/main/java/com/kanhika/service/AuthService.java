package com.kanhika.service;

import com.kanhika.dto.auth.AuthDTO;
import com.kanhika.dto.auth.LoginDTO;
import com.kanhika.dto.auth.RegisterDTO;
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
        // Check username uniqueness
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new RuntimeException("This username is already taken!");
        }

        // Check email uniqueness
        if (userRepository.existsByEmailIgnoreCaseAndDisabledFalse(request.email())) {
            throw new RuntimeException("There's already an account linked with this email address!");
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Return the JWT token
        String token = jwtService.generateToken(user.getUsername());
        return new AuthDTO(token);
    }
}
