package com.bookportal.api.service;

import com.bookportal.api.entity.Author;
import com.bookportal.api.entity.Book;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.AuthorRepository;
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
public class AuthorService {
    private final AuthorRepository authorRepository;

    public Author findByIdAndActiveTrue(Long id) {
        Optional<Author> byId = authorRepository.findByIdAndActiveTrue(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.AUTHOR.getValue());
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
    public Author deleteAuthor(Long id) {
        Optional<Author> byId = authorRepository.findByIdAndActiveTrue(id);
        if (byId.isPresent()) {
            Author author = byId.get();
            List<Book> books = author.getBooks();
            books.forEach(book -> {
                List<Author> authors = book.getAuthors();
                authors.remove(author);
                book.setAuthors(authors);
            });
            author.setBooks(books);
            author.setActive(false);
            return authorRepository.save(author);
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.AUTHOR.getValue());
    }

    public Optional<Author> findById(Long id) {
        Optional<Author> byId = authorRepository.findByIdAndActiveTrue(id);
        if (byId.isPresent()) {
            return byId;
        }
        throw new CustomNotFoundException(ExceptionItemsEnum.AUTHOR.getValue());
    }

    public Page<Author> getAllByPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return authorRepository.findAllByActiveTrue(pageable);
    }

}
