package com.bookportal.api.service;

import com.bookportal.api.entity.Quote;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserService userService;
    private final BookService bookService;

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Quote save(Quote quote, Long bookId) {
        Optional<Quote> byQuote = quoteRepository.findByQuote(quote.getQuote());
        if (byQuote.isPresent()) {
            throw new CustomAlreadyExistException(ExceptionItemsEnum.QUOTE.getValue());
        }
        quote.setBook(bookService.findByIdAndActiveTrueAndIsPublishedTrue(bookId));
        quote.setUser(userService.getCurrentUser().get());
        return quoteRepository.save(quote);
    }

    public Quote getRandomQuote() {
        Optional<Quote> random = quoteRepository.findRandomQuoteByActiveTrue();
        if (random.isPresent()) {
            return random.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.QUOTE.getValue());
    }

    public List<Quote> getAll() {
        return quoteRepository.findAllByActiveTrue();
    }

    public Page<Quote> getByPageSize(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quoteRepository.findAllByActiveTrue(pageable);
    }

    public Quote findByIdAndActiveTrue(Long id) {
        Optional<Quote> optionalQuote = quoteRepository.findQuoteByIdAndActiveTrue(id);
        if (!optionalQuote.isPresent()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.QUOTE.getValue());
        }
        return optionalQuote.get();
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean delete(Long id) {
        Optional<Quote> optionalQuote = quoteRepository.findQuoteByIdAndActiveTrue(id);
        if (!optionalQuote.isPresent()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.QUOTE.getValue());
        }
        optionalQuote.get().setActive(false);
        quoteRepository.save(optionalQuote.get());
        return !quoteRepository.findById(id).get().isActive();
    }

    public void increaseQuoteCount(Quote quote) {
        quote.setCount(quote.getCount() + 1);
        quoteRepository.save(quote);
    }

    public void decreaseQuoteCount(Quote quote) {
        quote.setCount(quote.getCount() - 1);
        quoteRepository.save(quote);
    }

    public List<Quote> findQuotestByIds(List<Long> ids) {
        return quoteRepository.findAllById(ids);
    }
}
