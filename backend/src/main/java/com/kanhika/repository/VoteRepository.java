package com.kanhika.repository;

import com.kanhika.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    // Get sum of votes
    @Query("""
        SELECT COALESCE(SUM(v.vote), 0)
        FROM Vote v
        WHERE v.id = :id
    """)
    int findSumVote(@Param("id") int id);
}
