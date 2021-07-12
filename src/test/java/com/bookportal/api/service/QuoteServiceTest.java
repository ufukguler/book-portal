package com.bookportal.api.service;

import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Quote;
import com.bookportal.api.entity.User;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.model.QuoteDTO;
import com.bookportal.api.repository.QuoteRepository;
import com.bookportal.api.util.mapper.QuoteMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(classes = QuoteService.class)
class QuoteServiceTest {

    @MockBean
    QuoteRepository quoteRepository;

    @MockBean
    BookService bookService;

    @MockBean
    UserService userService;

    @Autowired
    QuoteService quoteService;


    @Test
    void save() {
        QuoteDTO dto = new QuoteDTO();
        dto.setQuote("alinti");
        dto.setBookId(1L);
        Quote quote = QuoteMapper.quoteDtoToQuote(dto);

        Mockito.lenient()
                .when(quoteRepository.findByQuote(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(anyLong()))
                .thenReturn(new Book());
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(new User()));
        Mockito.lenient()
                .when(quoteRepository.save(quote))
                .thenReturn(quote);

        Quote savedQuote = quoteService.save(quote, 1L);
        assertNotNull(savedQuote.getUser());
        assertNotNull(savedQuote.getBook());
        assertEquals(dto.getQuote(), savedQuote.getQuote());
        assertEquals(quote.getQuote(), savedQuote.getQuote());
    }

    @Test
    void save_exist_quote() {
        QuoteDTO dto = new QuoteDTO();
        dto.setQuote("alinti");
        dto.setBookId(1L);
        Quote quote = QuoteMapper.quoteDtoToQuote(dto);
        Mockito.lenient()
                .when(quoteRepository.findByQuote(quote.getQuote()))
                .thenReturn(Optional.of(quote));
        assertThrows(CustomAlreadyExistException.class,
                () -> quoteService.save(quote, 1L));
    }

    @Test
    void save_notFound_book() {
        QuoteDTO dto = new QuoteDTO();
        dto.setQuote("alinti");
        dto.setBookId(1L);
        Quote quote = QuoteMapper.quoteDtoToQuote(dto);

        Mockito.lenient()
                .when(quoteRepository.findByQuote(anyString()))
                .thenReturn(Optional.empty());
        Mockito.lenient()
                .when(bookService.findByIdAndActiveTrueAndIsPublishedTrue(anyLong()))
                .thenThrow(CustomNotFoundException.class);
        Mockito.lenient()
                .when(userService.getCurrentUser())
                .thenReturn(Optional.of(new User()));
        Mockito.lenient()
                .when(quoteRepository.save(quote))
                .thenReturn(quote);

        assertThrows(CustomNotFoundException.class, () -> quoteService.save(quote, 1L));
    }

    @Test
    void getAll_emptyList() {
        Mockito.lenient()
                .when(quoteRepository.findAllByActiveTrue())
                .thenReturn(Collections.emptyList());

        List<Quote> quoteList = quoteService.getAll();

        assertTrue(quoteList.isEmpty());
        assertNotNull(quoteList);
    }

    @Test
    void getAll() {
        Mockito.lenient()
                .when(quoteRepository.findAllByActiveTrue())
                .thenReturn(Arrays.asList(new Quote(), new Quote()));

        List<Quote> quoteList = quoteService.getAll();

        assertFalse(quoteList.isEmpty());
        assertNotNull(quoteList);
        assertTrue(quoteList.size() > 0);
        assertNotNull(quoteList.get(0));
    }

    @Test
    void getByPageSize() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Quote> quotePage = new PageImpl<>(Collections.singletonList(new Quote()));

        Mockito.lenient()
                .when(quoteRepository.findAllByActiveTrue(pageable))
                .thenReturn(quotePage);

        Page<Quote> byPageSize = quoteService.getByPageSize(1, 2);
        assertEquals(byPageSize.getNumberOfElements(), 1);
        assertFalse(byPageSize.isEmpty());
    }

    @Test
    void getByPageSize_emptyPage() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Quote> quotePage = new PageImpl<>(Collections.emptyList());

        Mockito.lenient()
                .when(quoteRepository.findAllByActiveTrue(pageable))
                .thenReturn(quotePage);

        Page<Quote> byPageSize = quoteService.getByPageSize(1, 2);
        assertEquals(byPageSize.getNumberOfElements(), 0);
        assertTrue(byPageSize.isEmpty());
    }

    @Test
    void findByIdAndActiveTrue() {
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setQuote("test");
        quote.setUser(new User());
        quote.setBook(new Book());
        quote.setActive(true);

        Mockito.lenient()
                .when(quoteRepository.findQuoteByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(quote));

        Quote byId = quoteService.findByIdAndActiveTrue(1L);
        assertEquals(quote.getId(), byId.getId());
        assertEquals(quote.getQuote(), byId.getQuote());
        assertEquals(quote.getUser(), byId.getUser());
        assertEquals(quote.getBook(), byId.getBook());
        assertEquals(quote.isActive(), byId.isActive());
    }

    @Test
    void findByIdAndActiveTrue_notFoundException() {
        Mockito.lenient()
                .when(quoteRepository.findQuoteByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> quoteService.findByIdAndActiveTrue(1L));
    }

    @Test
    void delete() {
        Quote quoteActiveFalse = new Quote();
        quoteActiveFalse.setActive(false);

        Mockito.lenient()
                .when(quoteRepository.findById(anyLong()))
                .thenReturn(Optional.of(quoteActiveFalse));
        Mockito.lenient()
                .when(quoteRepository.findQuoteByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.of(new Quote()));

        boolean delete = quoteService.delete(1L);
        assertTrue(delete);
    }

    @Test
    void delete_notFoundException() {
        Mockito.lenient()
                .when(quoteRepository.findQuoteByIdAndActiveTrue(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> quoteService.delete(1L));
    }

    @Test
    void increaseQuoteCount() {
        int count = 5;
        Quote quote = new Quote();
        quote.setCount(count);

        Mockito.lenient()
                .when(quoteRepository.save(quote))
                .thenReturn(quote);

        quoteService.increaseQuoteCount(quote);
        assertEquals(count + 1, quote.getCount());
    }

    @Test
    void decreaseQuoteCount() {
        int count = 5;
        Quote quote = new Quote();
        quote.setCount(count);

        Mockito.lenient()
                .when(quoteRepository.save(quote))
                .thenReturn(quote);

        quoteService.decreaseQuoteCount(quote);
        assertEquals(count - 1, quote.getCount());
    }

    @Test
    void findQuotesByIds() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<Quote> quotes = Arrays.asList(new Quote(), new Quote());
        Mockito.lenient()
                .when(quoteRepository.findAllById(ids))
                .thenReturn(quotes);

        List<Quote> quotesByIds = quoteService.findQuotestByIds(ids);

        assertFalse(quotesByIds.isEmpty());
        assertEquals(quotes.size(), quotesByIds.size());
    }

    @Test
    void getRandomQuote() {
        Quote quote = new Quote();
        quote.setActive(true);
        quote.setQuote("myQuote");
        quote.setBook(new Book());
        quote.setCount(0);
        quote.setUser(new User());
        Mockito.lenient()
                .when(quoteRepository.findRandomQuoteByActiveTrue())
                .thenReturn(Optional.of(quote));
        Quote randomQuote = quoteService.getRandomQuote();
        assertNotNull(randomQuote);
        assertEquals(quote.getQuote(), randomQuote.getQuote());
        assertEquals(quote.getCount(), randomQuote.getCount());
        assertEquals(quote.getBook(), randomQuote.getBook());
    }

    @Test
    void getRandomQuote_throwsNotFoundException() {
        Mockito.lenient()
                .when(quoteRepository.findRandomQuoteByActiveTrue())
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> quoteService.getRandomQuote());
    }
}