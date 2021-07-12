package com.bookportal.api.service;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = AuthorService.class)
class AuthorServiceTest {

    @Autowired
    AuthorService authorService;

    @MockBean
    AuthorRepository authorRepository;

    @Test
    void findByIdAndActiveTrue() {
        Author author = new Author();
        author.setActive(true);
        author.setName("yazar");

        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(author));

        Author byId = authorService.findByIdAndActiveTrue(1L);

        assertTrue(byId.isActive());
        assertNotNull(byId);
        verify(authorRepository, times(1)).findByIdAndActiveTrue(anyLong());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void findByIdAndActiveTrue_exception() {
        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> authorService.findByIdAndActiveTrue(1L));
        verify(authorRepository, times(1)).findByIdAndActiveTrue(anyLong());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void saveAuthor() {
        Author author = new Author();
        author.setName("yazar");
        author.setAbout("hakkında");

        Mockito.lenient()
                .when(authorRepository.save(author))
                .thenReturn(author);

        Author savedAuthor = authorService.saveAuthor(author);
        assertEquals(savedAuthor.getName(), author.getName());
        assertEquals(savedAuthor.getAbout(), author.getAbout());
        verify(authorRepository, times(1)).save(any());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void deleteAuthor() {
        List<Book> bookList = new ArrayList<>();
        List<Author> authorList = new ArrayList<>();

        Book book = new Book();
        Author author = new Author();
        author.setName("yazar");
        author.setId(3L);

        bookList.add(book);
        authorList.add(author);
        book.setAuthors(authorList);
        author.setBooks(bookList);

        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(author));
        Mockito.lenient()
                .when(authorRepository.save(author))
                .thenReturn(author);

        Author deletedAuthor = authorService.deleteAuthor(anyLong());
        assertFalse(deletedAuthor.isActive());
        verify(authorRepository, times(1)).save(any());
        verify(authorRepository, times(1)).findByIdAndActiveTrue(anyLong());
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void deleteAuthor_exception() {
        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> authorService.deleteAuthor(1L));
    }

    @Test
    void findById() {
        Author author = new Author();
        author.setName("author");
        author.setAbout("about");
        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(author));
        Optional<Author> byId = authorService.findById(1L);
        assertTrue(byId.isPresent());
        assertEquals(author.getName(), byId.get().getName());
        assertEquals(author.getAbout(), byId.get().getAbout());
    }

    @Test
    void findById_exception() {
        Mockito.lenient()
                .when(authorRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> authorService.findById(1L));

    }

    @Test
    void getAllByPagination() {
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authorPage = new PageImpl<>(Arrays.asList(new Author(), new Author()));

        Mockito.lenient()
                .when(authorRepository.findAllByActiveTrue(pageable))
                .thenReturn(authorPage);

        Page<Author> allByPagination = authorService.getAllByPagination(page, size);
        assertFalse(allByPagination.isEmpty());
    }

    @Test
    void getAllByPagination_emptyList() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Author> authorPage = new PageImpl<>(Collections.emptyList());

        Mockito.lenient()
                .when(authorRepository.findAllByActiveTrue(pageable))
                .thenReturn(authorPage);

        Page<Author> allByPagination = authorService.getAllByPagination(1, 2);
        assertEquals(allByPagination.getNumberOfElements(), 0);
        assertTrue(allByPagination.isEmpty());
    }
}