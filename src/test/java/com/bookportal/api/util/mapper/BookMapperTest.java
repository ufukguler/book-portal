package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookUpdateDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookMapperTest {

    @Test
    void bookDTOtoBook() {
        BookDTO bookDTO = initBookDTO();
        Publisher publisher = new Publisher();
        publisher.setId(1L);
        List<Author> authorList = Arrays.asList(new Author());
        User user = new User();

        Book book = BookMapper.bookDTOtoBook(bookDTO, authorList, publisher, Optional.of(user));

        assertEquals(book.getName(), bookDTO.getTitle());
        assertEquals(book.getPage(), bookDTO.getPage());
        assertEquals(book.getYear(), bookDTO.getYear());
        assertEquals(book.getTag(), bookDTO.getTag());
        assertEquals(book.getPublisher().getId(), bookDTO.getPublisherId());
        assertEquals(user, book.getUser());
    }

    @Test
    void bookUpdateDTOtoBook() {
        Book initBook = initBook();
        BookUpdateDTO updateDTO = initBookUpdateDTO();
        Publisher publisher = new Publisher();
        publisher.setId(1L);
        List<Author> authorList = Arrays.asList(new Author());
        User user = new User();
        Book book = BookMapper.bookUpdateDTOtoBook(updateDTO, initBook, authorList, publisher, Optional.of(user));

        assertEquals(updateDTO.getTitle(), book.getName());
        assertEquals(updateDTO.getPage(), book.getPage());
        assertEquals(updateDTO.getYear(), book.getYear());
        assertEquals(updateDTO.getTag(), book.getTag());
        assertEquals(updateDTO.getIsPublished(), book.getIsPublished());
        assertEquals(publisher, book.getPublisher());
        assertEquals(user, book.getEditor());
        assertEquals(authorList, book.getAuthors());
        assertNotNull(book.getUser());

    }

    private BookDTO initBookDTO() {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("book");
        bookDTO.setPage(123);
        bookDTO.setYear(2000);
        bookDTO.setAuthorIds(new Long[]{1L, 2L});
        bookDTO.setTag("tag");
        bookDTO.setPublisherId(1L);
        return bookDTO;
    }

    private Book initBook() {
        Book book = new Book();
        book.setId(1L);
        book.setName("name");
        book.setAuthors(Collections.singletonList(new Author()));
        book.setPage(123);
        book.setPublisher(new Publisher());
        book.setYear(2000);
        book.setImageUrl("url.com");
        book.setUser(new User());
        book.setTag("tags");
        book.setEditor(new User());
        book.setIsPublished(false);
        return book;
    }

    private BookUpdateDTO initBookUpdateDTO() {
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.setTitle("book");
        bookUpdateDTO.setAuthorIds(new Long[]{1L, 2L});
        bookUpdateDTO.setPage(123);
        bookUpdateDTO.setPublisherId(1L);
        bookUpdateDTO.setYear(2000);
        bookUpdateDTO.setTag("tag");
        bookUpdateDTO.setIsPublished(false);
        return bookUpdateDTO;
    }
}