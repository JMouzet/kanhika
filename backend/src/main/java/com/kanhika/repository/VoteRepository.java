package com.kanhika.repository;

import com.kanhika.model.Comment;
import com.kanhika.model.User;
import com.kanhika.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Get sum of votes
    @Query("""
        SELECT COALESCE(SUM(v.vote), 0)
        FROM Vote v
        WHERE v.comment = :comment
    """)
    Integer findSumVote(@Param("comment") Comment comment);

    // Get user vote as an object
    // Can return null
    Optional<Vote> findVoteByCommentAndUser(Comment comment, User user);
}
