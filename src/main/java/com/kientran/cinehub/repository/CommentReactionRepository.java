package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Optional<CommentReaction> findByUserIdAndCommentId(Long userId, Long commentId);

    List<CommentReaction> findByCommentIdIn(List<Long> commentIds);

    List<CommentReaction> findByUserIdAndCommentIdIn(Long userId, List<Long> commentIds);

    long countByCommentIdAndReactionType(Long commentId, String reactionType);
}
