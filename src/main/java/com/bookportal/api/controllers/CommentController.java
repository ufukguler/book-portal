package com.bookportal.api.controllers;

import com.bookportal.api.entity.Comment;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.CommentDTO;
import com.bookportal.api.service.BookService;
import com.bookportal.api.service.CommentService;
import com.bookportal.api.service.UserService;
import com.bookportal.api.util.mapper.CommentMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final BookService bookService;
    private final UserService userService;

    @GetMapping
    @ApiOperation(value = "List book comments")
    public ResponseEntity<?> getCommentsByPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(commentService.findAllByPaginationAndActiveTrue(PageRequest.of(page, size)));
    }

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Save/update comment")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> saveComment(@Valid @RequestBody CommentDTO dto) {
        Optional<User> currentUser = userService.getCurrentUser();
        Comment comment = CommentMapper.commentDtoToComment(dto, currentUser.orElse(null), bookService.findByIdAndActiveTrueAndIsPublishedTrue(dto.getBookId()));
        return new ResponseEntity<>(commentService.save(comment), HttpStatus.CREATED);
    }

    @GetMapping("/book/{id}")
    @ApiOperation(value = "List comment by book Id")
    public ResponseEntity<?> getCommentsByBookAndPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size,
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Integer bookId) {
        return ResponseEntity.ok(commentService.findCommentsByBook(PageRequest.of(page, size), bookId));
    }
}
