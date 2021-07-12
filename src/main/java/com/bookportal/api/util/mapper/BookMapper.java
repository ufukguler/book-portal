package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookUpdateDTO;

import java.util.List;
import java.util.Optional;

public class BookMapper {
    public static Book bookDTOtoBook(BookDTO bookDTO, List<Author> authorList, Publisher publisher, Optional<User> optionalUser) {
        Book book = new Book();
        book.setName(bookDTO.getTitle());
        book.setAuthors(authorList);
        book.setPage(bookDTO.getPage());
        book.setPublisher(publisher);
        book.setYear(bookDTO.getYear());
        book.setImageUrl(bookDTO.getImageUrl());
        book.setUser(optionalUser.get());
        book.setTag(bookDTO.getTag());
        return book;
    }

    public static Book bookUpdateDTOtoBook(BookUpdateDTO bookUpdateDTO, Book book, List<Author> authorList, Publisher publisher, Optional<User> optionalEditor) {
        book.setName(bookUpdateDTO.getTitle());
        book.setAuthors(authorList);
        book.setPage(bookUpdateDTO.getPage());
        book.setPublisher(publisher);
        book.setYear(bookUpdateDTO.getYear());
        book.setImageUrl(bookUpdateDTO.getImageUrl());
        book.setTag(bookUpdateDTO.getTag());
        book.setEditor(optionalEditor.get());
        book.setIsPublished(bookUpdateDTO.getIsPublished());
        return book;
    }
}
