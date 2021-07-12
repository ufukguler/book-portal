package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.User;
import com.bookportal.api.entity.UserBook;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.enums.UserBookEnum;
import com.bookportal.api.repository.UserBookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserBookServiceTest {
    @Autowired
    UserBookService userBookService;

    @MockBean
    UserBookRepository userBookRepository;

    @MockBean
    UserService userService;

    @MockBean
    BookService bookService;

    @Test
    void save() {
        Long id = 1L;
        Long enumId = 1L; // WILL_READ
        UserBook userBook = initUserBook(id, enumId);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(new User()));
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(id))
                .thenReturn(userBook.getBook());
        Mockito.lenient()
                .when(userBookService.save(id, enumId))
                .thenReturn(userBook);

        UserBook save = userBookService.save(id, enumId);
        assertEquals(userBook.getType(), save.getType());
        assertEquals(userBook.getBook().getId(), save.getBook().getId());
    }

    @Test
    void save_updateAlreadyExist() {
        Long id = 1L;
        Long enumId = 1L; // WILL_READ
        UserBook userBook = initUserBook(id, enumId);
        userBook.setActive(false);
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(userBook.getUser()));
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(id))
                .thenReturn(userBook.getBook());
        Mockito.lenient()
                .when(userBookRepository.findByUser_IdAndBook_IdAndType(id, id, UserBookEnum.HAVE_READ))
                .thenReturn(Optional.of(userBook));
        Mockito.lenient()
                .when(userBookService.save(id, enumId))
                .thenReturn(userBook);

        UserBook save = userBookService.save(id, enumId);
        assertEquals(userBook.getType(), save.getType());
        assertEquals(userBook.getType(), save.getType());
        assertEquals(userBook.getBook().getId(), save.getBook().getId());
        assertFalse(userBook.isActive());
    }

    @Test
    void save_enumTypeNotExist() {
        assertThrows(CustomNotFoundException.class, () -> userBookService.save(1L, 5L));
    }

    @Test
    void save_userNotExist() {
        Long id = 1L;
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(id))
                .thenReturn(new Book());
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> userBookService.save(id, id));
    }

    @Test
    void findTypesForBook() {
        UserBook haveRead = initUserBook(1L, 1L);
        UserBook willRead = initUserBook(1L, 0L);
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(haveRead.getUser()));
        Mockito.lenient()
                .when(userBookRepository.findByUser_IdAndBook_IdAndType(1L, 1L, UserBookEnum.HAVE_READ))
                .thenReturn(Optional.of(haveRead));
        Mockito.lenient()
                .when(userBookRepository.findByUser_IdAndBook_IdAndType(1L, 1L, UserBookEnum.WILL_READ))
                .thenReturn(Optional.of(willRead));
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(1L))
                .thenReturn(haveRead.getBook());

        Map<String, Boolean> typesForBook = userBookService.findTypesForBook(1L);
        assertTrue(typesForBook.get(UserBookEnum.WILL_READ.getValue()));
        assertTrue(typesForBook.get(UserBookEnum.HAVE_READ.getValue()));
    }

    UserBook initUserBook(Long id, Long enumId) {
        Book book = new Book();
        book.setId(id);
        User user = new User();
        user.setId(id);
        UserBook userBook = new UserBook();
        userBook.setUser(user);
        userBook.setBook(book);
        userBook.setType(enumId == 0L ? UserBookEnum.WILL_READ : UserBookEnum.HAVE_READ);
        userBook.setActive(true);
        return userBook;
    }
}