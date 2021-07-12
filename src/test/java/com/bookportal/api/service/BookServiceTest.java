package com.bookportal.api.service;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookUpdateDTO;
import com.bookportal.api.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = BookService.class)
public class BookServiceTest {

    @Autowired
    BookService bookService;

    @MockBean
    PublisherService publisherService;

    @MockBean
    UserService userService;

    @MockBean
    BookRepository bookRepository;

    @MockBean
    AuthorService authorService;

    @BeforeEach
    public void beforeEach() {
        Mockito.lenient()
                .when(authorService.findByIdAndActiveTrue(anyLong()))
                .thenReturn(new Author());
        Mockito.lenient()
                .when(publisherService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(new Publisher());
        Mockito.lenient()
                .when(userService.findByIdAndActiveTrue(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new User()));
    }

    @Test
    public void save() {
        Book book = new Book();
        book.setName("test");

        BookDTO dto = new BookDTO();
        dto.setTitle("test");
        dto.setAuthorIds(new Long[]{1L});
        dto.setPublisherId(1L);

        User currentUser = new User();
        currentUser.setId(1L);
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(currentUser));
        Mockito.lenient()
                .when(bookRepository.save(any(Book.class)))
                .thenReturn(book);

        Book savedBook = bookService.save(dto);

        Assertions.assertEquals(savedBook.getName(), dto.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void update() {
        Book bookBeforeSave = new Book();
        bookBeforeSave.setName("before");
        bookBeforeSave.setActive(true);
        Book bookAfterSave = new Book();
        bookAfterSave.setName("after");
        BookUpdateDTO updateDTO = new BookUpdateDTO();
        updateDTO.setTitle("after");
        updateDTO.setAuthorIds(new Long[]{1L});
        updateDTO.setPublisherId(1L);

        User currentUser = new User();
        currentUser.setId(1L);
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(currentUser));
        Mockito.lenient()
                .when(bookRepository.findById(1L))
                .thenReturn(Optional.of(bookBeforeSave));
        Mockito.lenient()
                .when(userService.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(currentUser));
        Mockito.lenient()
                .when(bookRepository.save(any(Book.class)))
                .thenReturn(bookAfterSave);

        Book updatedBook = bookService.update(1L, updateDTO);

        Assertions.assertEquals(bookAfterSave.getName(), updatedBook.getName());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void update_CustomNotFoundException_editor() {
        Book bookBeforeSave = new Book();
        bookBeforeSave.setName("before");
        bookBeforeSave.setActive(true);
        Book bookAfterSave = new Book();
        bookAfterSave.setName("after");
        BookUpdateDTO updateDTO = new BookUpdateDTO();
        updateDTO.setTitle("after");
        updateDTO.setAuthorIds(new Long[]{1L});
        updateDTO.setPublisherId(1L);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(bookRepository.findById(1L))
                .thenReturn(Optional.of(bookBeforeSave));
        Mockito.lenient()
                .when(userService.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> bookService.update(1L, updateDTO));
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    public void update_CustomNotFoundException_notPresent() {
        Mockito.lenient()
                .when(bookRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> bookService.update(1L, new BookUpdateDTO()));
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    public void update_CustomNotFoundException_notActive() {
        Book book = new Book();
        book.setActive(false);

        Mockito.lenient()
                .when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        assertThrows(CustomNotFoundException.class, () -> bookService.update(1L, new BookUpdateDTO()));
        verify(bookRepository, times(0)).save(any(Book.class));
    }

    @Test
    void testGetByPageSize() {
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = new PageImpl<>(Collections.singletonList(new Book()));

        Mockito.lenient()
                .when(bookRepository.findAllByActiveTrueAndIsPublishedTrue(pageable))
                .thenReturn(bookPage);
        Page<Book> byPageSize = bookService.getByPageSize(page, size);
        Assertions.assertTrue(byPageSize.getSize() > 0);
        assertFalse(byPageSize.isEmpty());
    }

    @Test
    public void delete() {
        Book book = new Book();
        book.setActive(true);

        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(book));
        Mockito.lenient()
                .when(bookRepository.save(book))
                .thenReturn(book);

        bookService.delete(anyLong());

        assertFalse(book.isActive());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookRepository, times(1)).findByIdAndActiveTrue(anyLong());
    }

    @Test
    public void delete_BookNotFoundException() {
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());

        Throwable exception = Assertions
                .assertThrows(CustomNotFoundException.class,
                        () -> bookService.delete(anyLong()));

        Assertions.assertEquals(CustomNotFoundException.class, exception.getClass());
    }

    @Test
    void findByIdAndActiveTrue() {
        Book book = new Book();
        book.setName("test");
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(book));
        Book byIdAndActiveTrue = bookService.findByIdAndActiveTrue(1L);
        Assertions.assertEquals(book.getName(), byIdAndActiveTrue.getName());
    }

    @Test
    void findByIdAndActiveTrue_CustomNotFoundException() {
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> bookService.findByIdAndActiveTrue(1L));
    }

    @Test
    void findByNameOrAuthorName() {
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        String text = "arama";
        Pageable pageable = PageRequest.of(0, 5);
        PageImpl<Book> pageBooks = new PageImpl<>(books);
        Mockito.lenient()
                .when(bookRepository.findAllByActiveTrueAndNameIsContainingOrAuthors_NameIsContaining(pageable, text, text))
                .thenReturn(pageBooks);
        Page<Book> bookList = bookService.findByNameOrAuthorName(pageable, text);
        assertFalse(bookList.getContent().isEmpty());
        assertNotNull(bookList.getContent().get(0));
    }

    @Test
    void publishBook() {
        Book book = new Book();
        book.setIsPublished(false);
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(book));
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(new User()));
        bookService.publishBook(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void unPublishBook() {
        Book book = new Book();
        book.setIsPublished(true);
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(book));
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(new User()));
        bookService.unPublishBook(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void findByIdAndActiveTrueAndIsPublishedTrue() {
        Book book = new Book();
        book.setIsPublished(true);
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrueAndIsPublishedTrue(anyLong()))
                .thenReturn(Optional.of(book));
        Book publishedBook = bookService.findByIdAndActiveTrueAndIsPublishedTrue(1L);
        assertNotNull(publishedBook);
        assertTrue(publishedBook.getIsPublished());
    }

    @Test
    void findByIdAndActiveTrueAndIsPublishedTrue_throws_CustomNotFoundException() {
        Mockito.lenient()
                .when(bookRepository.findByIdAndActiveTrueAndIsPublishedTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class,
                () -> bookService.findByIdAndActiveTrueAndIsPublishedTrue(1L));
    }

    @Test
    void findByCategoryAndPageSize() {
        int page = 0;
        int size = 5;
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = new PageImpl<>(Collections.singletonList(new Book()));
        Mockito.lenient()
                .when(bookRepository.findByCategories_Id(pageable, categoryId))
                .thenReturn(books);
        Page<Book> byCategory = bookService.findByCategoryAndPageSize(page, size, categoryId);
        Assertions.assertTrue(byCategory.getSize() > 0);
        assertFalse(byCategory.isEmpty());
    }

    @Test
    void getLastXBooks() {
        int number = 1;
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book());
        PageImpl<Book> pageBooks = new PageImpl<>(bookList);
        Mockito.lenient()
                .when(bookRepository.findAll(PageRequest.of(0, number, Sort.by("id").descending())))
                .thenReturn(pageBooks);
        List<Book> lastXBooks = bookService.getLastXBooks(number);
        assertEquals(1, lastXBooks.size());
        assertNotNull(lastXBooks);
    }
}