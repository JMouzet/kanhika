package com.kanhika.service;

import com.kanhika.dto.user.*;
import com.kanhika.exception.ConflictException;
import com.kanhika.model.Block;
import com.kanhika.model.Follow;
import com.kanhika.model.User;
import com.kanhika.repository.BlockRepository;
import com.kanhika.repository.FollowRepository;
import com.kanhika.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FollowRepository followRepository;
    private final BlockRepository blockRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FollowRepository followRepository,
                       BlockRepository blockRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.followRepository = followRepository;
        this.blockRepository = blockRepository;
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
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        return new UserSelfDTO(
                user.getUsername(),
                user.getBio(),
                user.getEmail(),
                user.getExp(),
                user.getFlame(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UserBioDTO patchUserBio(String username,
                                   UserBioDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

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
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

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
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        user.setEmail(request.email());
        userRepository.save(user);

        return new UserEmailDTO(
                user.getEmail()
        );
    }

    public void patchUserPassword(String username,
                                  UserPasswordDTO request) {

        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void disableUser(String username) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        user.setDisabled(true);
        userRepository.save(user);
    }

    public List<UserPublicDTO> getFollowerUsers(String username) {
        List<User> followers = followRepository.findAllFollowerUsers(username);
        return followers.stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getBio(),
                        user.getExp(),
                        user.getFlame(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public List<UserPublicDTO> getFollowedUsers(String username) {
        List<User> following = followRepository.findAllFollowedUsers(username);
        return following.stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getBio(),
                        user.getExp(),
                        user.getFlame(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public void followUser(String myUsername, String targetUsername) {
        User myUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        User targetUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        int myId = myUser.getId();
        int targetId = targetUser.getId();

        if (myId == targetId) {
            throw new IllegalArgumentException("Following itself is not allowed.");
        }

        // Check if record not exists
        if (!followRepository.existsByUserIdAndFollowId(myId, targetId)) {
            Follow follow = new Follow();
            follow.setUser(myUser);
            follow.setFollow(targetUser);
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollowUser(String myUsername, String targetUsername) {
        User myUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        User targetUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        int myId = myUser.getId();
        int targetId = targetUser.getId();

        if (myId == targetId) {
            throw new IllegalArgumentException("Unfollowing itself won't do anything.");
        }

        followRepository.deleteByUserIdAndFollowId(myId, targetId);
    }

    public List<UserPublicDTO> getBlockedUsers(String username) {
        List<User> blocked = blockRepository.findAllBlockedUsers(username);
        return blocked.stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getBio(),
                        user.getExp(),
                        user.getFlame(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public void blockUser(String myUsername, String targetUsername) {
        User myUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        User targetUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        int myId = myUser.getId();
        int targetId = targetUser.getId();

        if (myId == targetId) {
            throw new IllegalArgumentException("Blocking itself is not allowed.");
        }

        // Check if record not exists
        if (!blockRepository.existsByUserIdAndBlockId(myId, targetId)) {
            Block block = new Block();
            block.setUser(myUser);
            block.setBlock(targetUser);
            blockRepository.save(block);
        }
    }

    @Transactional
    public void unblockUser(String myUsername, String targetUsername) {
        User myUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(myUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        User targetUser = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(targetUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        int myId = myUser.getId();
        int targetId = targetUser.getId();

        if (myId == targetId) {
            throw new IllegalArgumentException("Unblocking itself won't do anything.");
        }

        blockRepository.deleteByUserIdAndBlockId(myId, targetId);
    }

    public List<UserPublicDTO> searchUsers(String input) {
        List<User> result = userRepository.findAllByUsernameIgnoreCaseContaining(input);

        return result.stream()
                .map(user -> new UserPublicDTO(
                        user.getUsername(),
                        user.getBio(),
                        user.getExp(),
                        user.getFlame(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }
}
