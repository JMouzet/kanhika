package com.kanhika.controller;

import com.kanhika.dto.user.*;
import com.kanhika.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
}
