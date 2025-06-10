package com.kanhika.repository;

import com.kanhika.model.Block;
import com.kanhika.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    // Return the list of blocked accounts
    @Query("SELECT b.block FROM Block b WHERE b.user.username = :username")
    List<User> findAllBlockedUsers(@Param("username") String username);

    // Return the list of blockers
    @Query("SELECT b.user FROM Block b WHERE b.block.username = :username")
    List<User> findAllBlockerUsers(@Param("username") String username);

    // Check if the user is already blocking another user
    boolean existsByUserIdAndBlockId(int userId, int blockId);

    // Delete the block entry if userId and blockId match
    void deleteByUserIdAndBlockId(int userId, int blockId);
}
