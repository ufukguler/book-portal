package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Comment;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.CommentDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void commentDtoToComment() {
        CommentDTO dto = new CommentDTO();
        User user = new User();
        Book book = new Book();
        book.setId(3L);
        dto.setComment("my comment");
        dto.setBookId(book.getId());

        Comment comment = CommentMapper.commentDtoToComment(dto, user, book);

        assertEquals(dto.getComment(), comment.getComment());
        assertEquals(dto.getBookId(), comment.getBook().getId());
        assertEquals(user, comment.getUser());
        assertEquals(book, comment.getBook());


    }
}