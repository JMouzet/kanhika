package com.kanhika.service;

import com.kanhika.dto.comment.CommentDTO;
import com.kanhika.dto.comment.CommentPostDTO;
import com.kanhika.exception.NoModificationsException;
import com.kanhika.exception.ResourceNotFoundException;
import com.kanhika.exception.UnauthorizedException;
import com.kanhika.model.Comment;
import com.kanhika.model.Kanji;
import com.kanhika.model.User;
import com.kanhika.repository.CommentRepository;
import com.kanhika.repository.KanjiRepository;
import com.kanhika.repository.UserRepository;
import com.kanhika.repository.VoteRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final KanjiRepository kanjiRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          VoteRepository voteRepository,
                          KanjiRepository kanjiRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.kanjiRepository = kanjiRepository;
        this.userRepository = userRepository;
    }

    public List<CommentDTO> getComments(String kanji) {
        Kanji kanjiObj = kanjiRepository.findByKanji(kanji)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found."));

        List<Comment> comments = commentRepository.findAllByKanjiAndDeletedFalse(kanjiObj);

        return makeListCommentDTO(comments);
    }

    public CommentDTO sendComment(String username,
                                  String kanji,
                                  CommentPostDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Kanji kanjiObj = kanjiRepository.findByKanji(kanji)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found."));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setKanji(kanjiObj);
        comment.setMessage(request.message());
        commentRepository.save(comment);
        // TODO: auto upvote the post

        return makeCommentDTO(comment);
    }

    public CommentDTO editComment(String username,
                                  int id,
                                  CommentPostDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Comment comment = commentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));

        if (!user.equals(comment.getUser())) {
            throw new UnauthorizedException("You are not allowed to edit this comment.");
        }
        if (comment.getMessage().equals(request.message())) {
            throw new NoModificationsException("");
        }

        comment.setMessage(request.message());
        commentRepository.save(comment);

        return makeCommentDTO(comment);
    }

    public void deleteComment(String username,
                              int id) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Comment comment = commentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));

        if (!user.equals(comment.getUser())) {
            throw new UnauthorizedException("You are not allowed to delete this comment.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }


    private List<CommentDTO> makeListCommentDTO(List<Comment> comments) {
        return comments.stream()
                .map(this::makeCommentDTO)
                .toList();
    }

    private CommentDTO makeCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getMessage(),
                voteRepository.findSumVote(comment.getId()),
                false,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
