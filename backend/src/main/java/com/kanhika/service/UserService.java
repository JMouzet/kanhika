package com.kanhika.service;

import com.kanhika.dto.user.*;
import com.kanhika.exception.ConflictException;
import com.kanhika.model.User;
import com.kanhika.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserPublicDTO getUser(String username) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new UserPublicDTO(
                user.getUsername(),
                user.getBio(),
                user.getExp(),
                user.getFlame(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UserSelfDTO getUserSelf(String username) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new UserSelfDTO(
                user.getUsername(),
                user.getBio(),
                user.getEmail()
        );
    }

    public UserBioDTO patchUserBio(String username,
                                   UserBioDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        user.setBio(request.bio());
        userRepository.save(user);

        return new UserBioDTO(
                user.getBio()
        );
    }

    public UserUsernameDTO patchUserUsername(String username,
                                             UserUsernameDTO request) {
        // Check username uniqueness
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ConflictException("This username is already taken.");
        }

        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        user.setUsername(request.username());
        userRepository.save(user);

        return new UserUsernameDTO(
                user.getUsername()
        );
    }

    public UserEmailDTO patchUserEmail(String username,
                                       UserEmailDTO request) {
        // Check email uniqueness
        if (userRepository.existsByEmailUsedOrBanned(request.email())) {
            throw new ConflictException("There is already an account linked with this email address.");
        }

        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        user.setEmail(request.email());
        userRepository.save(user);

        return new UserEmailDTO(
                user.getEmail()
        );
    }

    public void patchUserPassword(String username,
                                  UserPasswordDTO request) {

        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
