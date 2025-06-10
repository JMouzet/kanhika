package com.kanhika.repository;

import com.kanhika.model.Follow;
import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    // Return the list of followed accounts
    @Query("SELECT f.follow FROM Follow f WHERE f.user.username = :username")
    List<User> findAllFollowedUsers(@Param("username") String username);

    // Return the list of followers
    @Query("SELECT f.user FROM Follow f WHERE f.follow.username = :username")
    List<User> findAllFollowerUsers(@Param("username") String username);

    // Check if the user is already following another user
    boolean existsByUserIdAndFollowId(int userId, int followId);

    // Delete the follow entry if userId and followId match
    void deleteByUserIdAndFollowId(int userId, int followId);
}
