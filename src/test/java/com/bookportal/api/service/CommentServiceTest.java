package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Comment;
import com.bookportal.api.entity.User;
import com.bookportal.api.repository.CommentRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CommentService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @MockBean
    CommentRepository commentRepository;

    @Test
    @Order(1)
    void save() {
        Book book = new Book();
        book.setId(3L);
        User user = new User();
        user.setId(2L);
        Comment comment = new Comment();
        comment.setComment("comment");
        comment.setUser(user);
        comment.setBook(book);

        Mockito.lenient()
                .when(commentRepository.save(comment))
                .thenReturn(comment);

        Comment savedComment = commentService.save(comment);
        assertEquals(comment.getComment(), savedComment.getComment());
        assertNotNull(savedComment.getUser());
        assertNotNull(savedComment.getBook());
    }

    @Test
    @Order(2)
    void save_updatePreviousComment() {
        Book book = new Book();
        book.setId(3L);
        User user = new User();
        user.setId(2L);
        Comment comment = new Comment();
        comment.setComment("updated comment");
        comment.setUser(user);
        comment.setBook(book);

        Mockito.lenient()
                .when(commentRepository.findByBook_IdAndUser_Id(book.getId(), user.getId()))
                .thenReturn(Optional.of(comment));

        Mockito.lenient()
                .when(commentRepository.save(comment))
                .thenReturn(comment);

        Comment savedComment = commentService.save(comment);
        assertEquals(comment.getComment(), savedComment.getComment());
        assertNotNull(savedComment.getUser());
        assertNotNull(savedComment.getBook());
    }

    @Test
    void findAllByActiveTrue() {
        Comment comment = new Comment();
        comment.setActive(true);

        Mockito.lenient()
                .when(commentRepository.findAllByActiveTrue())
                .thenReturn(Collections.singletonList(comment));
        List<Comment> allByActiveTrue = commentService.findAllByActiveTrue();
        assertFalse(allByActiveTrue.isEmpty());
        assertTrue(allByActiveTrue.size() > 0);
    }

    @Test
    void findAllByActiveTrue_emptyArray() {
        Mockito.lenient()
                .when(commentRepository.findAllByActiveTrue())
                .thenReturn(new ArrayList<>());

        List<Comment> allByActiveTrue = commentService.findAllByActiveTrue();
        assertTrue(allByActiveTrue.isEmpty());
    }

    @Test
    void findAllByPaginationAndActiveTrue() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Comment> pageComment = new PageImpl<>(Collections.singletonList(new Comment()));
        Mockito.lenient()
                .when(commentRepository.findAllByActiveTrue(pageable))
                .thenReturn(pageComment);

        Page<Comment> pagination = commentService.findAllByPaginationAndActiveTrue(pageable);

        assertEquals(pagination.getNumberOfElements(), 1);
        assertFalse(pagination.isEmpty());
    }

    @Test
    void findAllByPaginationAndActiveTrue_emptyPage() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Comment> pageComment = new PageImpl<>(Collections.emptyList());
        Mockito.lenient()
                .when(commentRepository.findAllByActiveTrue(pageable))
                .thenReturn(pageComment);

        Page<Comment> pagination = commentService.findAllByPaginationAndActiveTrue(pageable);

        assertEquals(pagination.getNumberOfElements(), 0);
        assertTrue(pagination.isEmpty());
    }

    @Test
    void findCommentsByBook() {
        int page = 0;
        int size = 5;
        int bookId = 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = new PageImpl<>(Collections.singletonList(new Comment()));
        Mockito.lenient()
                .when(commentRepository.findByBook_Id(pageable, 1L))
                .thenReturn(comments);
        Page<Comment> commentsByBook = commentService.findCommentsByBook(pageable, bookId);
        Assertions.assertTrue(commentsByBook.getSize() > 0);
        assertFalse(commentsByBook.isEmpty());
    }
}