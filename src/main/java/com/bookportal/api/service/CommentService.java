package com.bookportal.api.service;

import com.bookportal.api.entity.Comment;
import com.bookportal.api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;


    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Comment save(Comment comment) {
        Long bookId = comment.getBook().getId();
        Long userId = comment.getUser().getId();
        Optional<Comment> optional = commentRepository.findByBook_IdAndUser_Id(bookId, userId);
        if (optional.isPresent()) {
            Comment updateComment = optional.get();
            updateComment.setComment(comment.getComment());
            return commentRepository.save(updateComment);
        }
        return commentRepository.save(comment);
    }

    public List<Comment> findAllByActiveTrue() {
        return commentRepository.findAllByActiveTrue();
    }

    public Page<Comment> findAllByPaginationAndActiveTrue(Pageable pageable) {
        return commentRepository.findAllByActiveTrue(pageable);
    }

    public Page<Comment> findCommentsByBook(Pageable pageable, int bookId) {
        return commentRepository.findByBook_Id(pageable, (long) bookId);
    }
}
