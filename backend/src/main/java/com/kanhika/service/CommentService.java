package com.kanhika.service;

import com.kanhika.dto.comment.CommentDTO;
import com.kanhika.dto.comment.CommentPostDTO;
import com.kanhika.exception.NoModificationsException;
import com.kanhika.exception.ResourceNotFoundException;
import com.kanhika.exception.UnauthorizedException;
import com.kanhika.model.Comment;
import com.kanhika.model.Kanji;
import com.kanhika.model.User;
import com.kanhika.model.Vote;
import com.kanhika.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final KanjiRepository kanjiRepository;
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    public CommentService(CommentRepository commentRepository,
                          VoteRepository voteRepository,
                          KanjiRepository kanjiRepository,
                          UserRepository userRepository,
                          BlockRepository blockRepository) {
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.kanjiRepository = kanjiRepository;
        this.userRepository = userRepository;
        this.blockRepository = blockRepository;
    }

    public List<CommentDTO> getComments(String username,
                                        String kanji) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Kanji kanjiObj = kanjiRepository.findByKanji(kanji)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found."));

        List<Comment> comments = commentRepository.findAllByKanjiAndDeletedFalse(kanjiObj);

        return makeListCommentDTO(comments, user);
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

        voteComment(username, comment.getId(), true);

        return makeCommentDTO(comment, user);
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

        return makeCommentDTO(comment, user);
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

    public void voteComment(String username,
                            int id,
                            boolean isUpvote) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Comment comment = commentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));

        Vote vote = voteRepository.findVoteByCommentAndUser(comment, user)
                .orElse(new Vote());

        int voteValue = -1;
        if (isUpvote)
            voteValue = 1;


        vote.setComment(comment);
        vote.setUser(user);
        vote.setVote(voteValue);
        voteRepository.save(vote);
    }

    public void removeVoteComment(String username,
                                  int id) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));
        Comment comment = commentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));

        Vote vote = voteRepository.findVoteByCommentAndUser(comment, user)
                .orElseThrow(() -> new NoModificationsException(""));

        voteRepository.delete(vote);
    }


    private List<CommentDTO> makeListCommentDTO(List<Comment> comments,
                                                User user) {
        return comments.stream()
                .map((comment) -> makeCommentDTO(comment, user))
                .toList();
    }

    private CommentDTO makeCommentDTO(Comment comment,
                                      User user) {
        Vote vote = voteRepository.findVoteByCommentAndUser(comment, comment.getUser())
                .orElseGet(() -> {
                    Vote empty = new Vote();
                    empty.setComment(comment);
                    empty.setUser(comment.getUser());
                    empty.setVote(0);
                    return empty;
                });
        if (blockRepository.existsByUserIdAndBlockId(comment.getUser().getId(), user.getId()))
            comment.setMessage("This user blocked you.");
        if (blockRepository.existsByUserIdAndBlockId(user.getId(), comment.getUser().getId()))
            comment.setMessage("This user is blocked.");

        return new CommentDTO(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getMessage(),
                voteRepository.findSumVote(comment),
                vote.getVote(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
