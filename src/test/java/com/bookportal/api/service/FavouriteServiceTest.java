package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Favourite;
import com.bookportal.api.entity.Quote;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.FavouriteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(classes = FavouriteService.class)
class FavouriteServiceTest {

    @MockBean
    FavouriteRepository favouriteRepository;

    @MockBean
    UserService userService;

    @MockBean
    QuoteService quoteService;

    @Autowired
    FavouriteService favouriteService;

    @Test
    void addQuoteToFavourite_updateOldFavouriteRecord() {
        Book book = initBook();
        User user = initUser();
        Quote quote = initQuote(book, user);
        Favourite favourite = initFavourite(user, quote);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(quoteService.findByIdAndActiveTrue(anyLong()))
                .thenReturn(quote);
        Mockito.lenient()
                .when(favouriteRepository.findByQuoteIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(favourite));
        Mockito.lenient()
                .when(favouriteRepository.save(any()))
                .thenReturn(favourite);

        boolean bool = favouriteService.addQuoteToFavourite(1L);
        assertTrue(bool);

    }

    @Test
    void addQuoteToFavourite_insertNewFavouriteRecord() {
        Book book = initBook();
        User user = initUser();
        Quote quote = initQuote(book, user);

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(quoteService.findByIdAndActiveTrue(anyLong()))
                .thenReturn(quote);
        Mockito.lenient()
                .when(favouriteRepository.findByQuoteIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty()); // empty //

        boolean bool = favouriteService.addQuoteToFavourite(1L);
        assertTrue(bool);

    }

    @Test
    void addQuoteToFavourite_quoteNotFound() {
        User user = initUser();

        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(user));
        Mockito.lenient()
                .when(quoteService.findByIdAndActiveTrue(anyLong()))
                .thenThrow(CustomNotFoundException.class);

        assertThrows(CustomNotFoundException.class, () -> favouriteService.addQuoteToFavourite(1L));

    }

    @Test
    void findFavouritesByQuote() {
        Mockito.lenient()
                .when(favouriteRepository.findAllByQuoteIdAndActiveTrue(anyLong()))
                .thenReturn(Arrays.asList(new Favourite()));
        List<Favourite> favourites = favouriteService.findFavouritesByQuote(1L);
        assertTrue(favourites.size() > 0);
        assertTrue(favourites != null);
        assertFalse(favourites.isEmpty());
    }

    @Test
    void findFavouritesByQuote_emptyList() {
        Mockito.lenient()
                .when(favouriteRepository.findAllByQuoteIdAndActiveTrue(anyLong()))
                .thenReturn(Collections.emptyList());
        List<Favourite> favourites = favouriteService.findFavouritesByQuote(1L);
        assertTrue(favourites != null);
        assertTrue(favourites.isEmpty());
    }

    @Test
    void findFavouritesByUser() {
        Mockito.lenient().when(favouriteRepository.findAllByUserIdAndActiveTrue(anyLong()))
                .thenReturn(Arrays.asList(new Favourite()));

        List<Favourite> favouritesByUser = favouriteService.findFavouritesByUser(1L);
        assertFalse(favouritesByUser.isEmpty());
    }

    @Test
    void findFavouritesByUser_emptyList() {
        Mockito.lenient().when(favouriteRepository.findAllByUserIdAndActiveTrue(anyLong()))
                .thenReturn(Arrays.asList());

        List<Favourite> favouritesByUser = favouriteService.findFavouritesByUser(1L);
        assertTrue(favouritesByUser.isEmpty());
    }

    private Favourite initFavourite(User user, Quote quote) {
        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setQuote(quote);
        favourite.setActive(true);
        favourite.setId(1L);
        return favourite;
    }

    private Quote initQuote(Book book, User user) {
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setCount(11);
        quote.setActive(true);
        quote.setBook(book);
        quote.setUser(user);
        return quote;
    }

    private User initUser() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);
        return user;
    }

    private Book initBook() {
        Book book = new Book();
        book.setName("book");
        book.setId(1L);
        return book;
    }
}