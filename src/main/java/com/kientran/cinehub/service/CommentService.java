package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.request.CommentRequest;
import com.kientran.cinehub.dto.response.CommentResponse;
import com.kientran.cinehub.entity.Comment;
import com.kientran.cinehub.entity.Movie;
import com.kientran.cinehub.entity.User;
import com.kientran.cinehub.repository.CommentRepository;
import com.kientran.cinehub.repository.MovieRepository;
import com.kientran.cinehub.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {

    CommentRepository commentRepository;
    MovieRepository movieRepository;
    UserRepository userRepository;

    public CommentResponse addComment(CommentRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .movie(movie)
                .user(user)
                .parentComment(parentComment)
                .build();

        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    public List<CommentResponse> getCommentsByMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("Movie not found");
        }
        
        return commentRepository.findByMovieId(movieId).stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        List<CommentResponse> repliesDTO = null;
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            repliesDTO = comment.getReplies().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .username(comment.getUser().getUsername())
                .userAvatar(comment.getUser().getAvatar())
                .replies(repliesDTO)
                .build();
    }
}