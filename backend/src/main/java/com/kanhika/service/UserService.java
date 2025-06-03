package com.kanhika.service;

import com.kanhika.dto.user.UserPublicDTO;
import com.kanhika.model.User;
import com.kanhika.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
