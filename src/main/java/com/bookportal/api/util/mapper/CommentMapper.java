package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Comment;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.CommentDTO;

public class CommentMapper {

    public static Comment commentDtoToComment(CommentDTO dto, User user, Book book) {
        Comment comment = new Comment();
        comment.setComment(dto.getComment());
        comment.setBook(book);
        comment.setUser(user);
        return comment;
    }
}
