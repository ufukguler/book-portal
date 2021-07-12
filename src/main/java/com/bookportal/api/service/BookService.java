package com.bookportal.api.service;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.entity.Publisher;
import com.bookportal.api.entity.User;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookUpdateDTO;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.util.mapper.BookMapper;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final PublisherService publisherService;
    private final UserService userService;
    private final AuthorService authorService;

    public Page<Book> getByPageSize(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllByActiveTrueAndIsPublishedTrue(pageable);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Book delete(Long id) {
        Optional<Book> byId = bookRepository.findByIdAndActiveTrue(id);
        if (byId.isPresent()) {
            Book book = byId.get();
            book.setAuthors(new ArrayList<>());
            book.setActive(false);
            return bookRepository.save(book);
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.BOOK.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Book save(BookDTO bookDTO) {
        Book book = checkInputsAndInitBook(bookDTO);
        book.setIsPublished(false);
        return bookRepository.save(book);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Book update(Long id, BookUpdateDTO bookUpdateDTO) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (!optionalBook.isPresent() || !optionalBook.get().isActive()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.BOOK.getValue());
        }
        Book book = checkInputsAndInitBookUpdate(id, bookUpdateDTO);
        return bookRepository.save(book);
    }

    private Book checkInputsAndInitBook(BookDTO bookDTO) {
        List<Author> authorList = new ArrayList<>();
        for (int i = 0; i < bookDTO.getAuthorIds().length; i++) {
            authorList.add(authorService.findByIdAndActiveTrue((bookDTO.getAuthorIds()[i])));
        }
        Publisher publisher = publisherService.findById(bookDTO.getPublisherId());
        return BookMapper.bookDTOtoBook(bookDTO, authorList, publisher, userService.getCurrentUser());
    }

    private Book checkInputsAndInitBookUpdate(Long id, BookUpdateDTO bookUpdateDTO) {
        List<Author> authorList = new ArrayList<>();
        for (Long authorId : bookUpdateDTO.getAuthorIds()) {
            authorList.add(authorService.findByIdAndActiveTrue(authorId));
        }
        Publisher publisher = publisherService.findById(bookUpdateDTO.getPublisherId());
        Optional<User> optionalEditor = userService.getCurrentUser();
        if (!optionalEditor.isPresent()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.EDITOR.getValue());
        }
        return BookMapper.bookUpdateDTOtoBook(bookUpdateDTO, bookRepository.findById(id).get(), authorList, publisher, optionalEditor);
    }

    public Book findByIdAndActiveTrueAndIsPublishedTrue(Long id) {
        Optional<Book> byId = bookRepository.findByIdAndActiveTrueAndIsPublishedTrue(id);
        if (!byId.isPresent()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.BOOK.getValue());
        }
        return byId.get();
    }

    public Book findByIdAndActiveTrue(Long id) {
        Optional<Book> byId = bookRepository.findByIdAndActiveTrue(id);
        if (!byId.isPresent()) {
            throw new CustomNotFoundException(ExceptionItemsEnum.BOOK.getValue());
        }
        return byId.get();
    }

    public Page<Book> findByNameOrAuthorName(Pageable pageable, String text) {
        return bookRepository.findAllByActiveTrueAndNameIsContainingOrAuthors_NameIsContaining(pageable, text, text);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean publishBook(Long id) {
        Book book = findByIdAndActiveTrue(id);
        book.setIsPublished(true);
        book.setEditor(userService.getCurrentUser().get());
        bookRepository.save(book);
        return true;

    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public boolean unPublishBook(Long id) {
        Book book = findByIdAndActiveTrue(id);
        book.setEditor(userService.getCurrentUser().get());
        book.setIsPublished(false);
        bookRepository.save(book);
        return true;
    }

    public Page<Book> findByCategoryAndPageSize(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByCategories_Id(pageable, categoryId);
    }

    public List<Book> getLastXBooks(int number) {
        return bookRepository.findAll(PageRequest.of(0, number, Sort.by("id").descending())).getContent();
    }
}
