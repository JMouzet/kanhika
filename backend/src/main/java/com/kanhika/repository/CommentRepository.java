package com.kanhika.repository;

import com.kanhika.model.Comment;
import com.kanhika.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Get all comments from a kanji page
    List<Comment> findAllByKanji(@Param("kanji") Kanji kanji);
}
