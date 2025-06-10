package com.kanhika.controller;

import com.kanhika.dto.user.*;
import com.kanhika.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<UserPublicDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @GetMapping("/me")
    public ResponseEntity<UserSelfDTO> getUserSelf(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserSelf(userDetails.getUsername()));
    }

    @PatchMapping("/me/bio")
    public ResponseEntity<UserBioDTO> patchUserBio(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestBody @Valid UserBioDTO request) {
        return ResponseEntity.ok(userService.patchUserBio(userDetails.getUsername(), request));
    }

    @PatchMapping("/me/username")
    public ResponseEntity<UserUsernameDTO> patchUserUsername(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody @Valid UserUsernameDTO request) {
        return ResponseEntity.ok(userService.patchUserUsername(userDetails.getUsername(), request));
    }

    @PatchMapping("/me/email")
    public ResponseEntity<UserEmailDTO> patchUserEmail(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody @Valid UserEmailDTO request) {
        return ResponseEntity.ok(userService.patchUserEmail(userDetails.getUsername(), request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> patchUserPassword(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody @Valid UserPasswordDTO request) {
        userService.patchUserPassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> disableUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.disableUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserPublicDTO>> getFollowerUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getFollowerUsers(userDetails.getUsername()));
    }

    @GetMapping("/following")
    public ResponseEntity<List<UserPublicDTO>> getFollowedUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getFollowedUsers(userDetails.getUsername()));
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable String username) {
        userService.followUser(userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follow/{username}")
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String username) {
        userService.unfollowUser(userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/block")
    public ResponseEntity<List<UserPublicDTO>> getBlockedUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getBlockedUsers(userDetails.getUsername()));
    }

    @PostMapping("/block/{username}")
    public ResponseEntity<Void> blockUser(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable String username) {
        // Unfollow the user before blocking
        userService.unfollowUser(userDetails.getUsername(), username);
        userService.blockUser(userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/block/{username}")
    public ResponseEntity<Void> unblockUser(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable String username) {
        userService.unblockUser(userDetails.getUsername(), username);
        return ResponseEntity.noContent().build();
    }
}
