package com.bookportal.api.controllers;

import com.bookportal.api.entity.Book;
import com.bookportal.api.model.BookDTO;
import com.bookportal.api.model.BookResponseDTO;
import com.bookportal.api.model.BookUpdateDTO;
import com.bookportal.api.model.enums.UserBookEnum;
import com.bookportal.api.service.BookService;
import com.bookportal.api.service.UserBookService;
import com.bookportal.api.service.VoteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final VoteService voteService;
    private final UserBookService userBookService;

    @GetMapping
    @ApiOperation(value = "List books")
    public ResponseEntity<?> getBooksByPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(bookService.getByPageSize(page, size));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Save book")
    public ResponseEntity<?> saveBook(@Valid @RequestBody BookDTO bookDTO) {
        return new ResponseEntity<>(bookService.save(bookDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get book by Id")
    public ResponseEntity<?> findBookById(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        Book book = bookService.findByIdAndActiveTrueAndIsPublishedTrue(id);
        Map<String, Boolean> map = userBookService.findTypesForBook(id);
        BookResponseDTO dto = new BookResponseDTO();
        dto.setBook(book);
        dto.setHaveRead(map.get(UserBookEnum.HAVE_READ.getValue()));
        dto.setWillRead(map.get(UserBookEnum.WILL_READ.getValue()));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Update book")
    public ResponseEntity<?> updateBook(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id,
            @Valid @RequestBody BookUpdateDTO bookUpdateDTO) {
        return ResponseEntity.ok(bookService.update(id, bookUpdateDTO));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Delete book")
    public ResponseEntity<?> deleteBook(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.delete(id));
    }

    @PostMapping("/{id}/vote")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Vote book")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> voteBook(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long bookId,
            @ApiParam(defaultValue = "1", allowableValues = "1,2,3,4,5", required = true) @RequestParam("vote") int vote) {
        if (vote < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Must be greater than 1.");
        }
        if (vote > 5) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Can not be greater than 5");
        }
        return ResponseEntity.ok(voteService.voteBook(bookService.findByIdAndActiveTrueAndIsPublishedTrue(bookId), vote));
    }

    @PostMapping("/search")
    @ApiOperation(value = "Search by book or author's name")
    public ResponseEntity<?> findBookByNameOrAuthorName(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size,
            @ApiParam(defaultValue = "victor hugo", required = true) @RequestParam(name = "text") String text) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bookService.findByNameOrAuthorName(pageable, text));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Publish book")
    public ResponseEntity<?> publishBook(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long bookId) {
        return ResponseEntity.ok(bookService.publishBook(bookId));
    }

    @PostMapping("/{id}/unPublish")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Unublish book")
    public ResponseEntity<?> unPublishBook(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long bookId) {
        return ResponseEntity.ok(bookService.unPublishBook(bookId));
    }

    @PostMapping("/{id}/read")
    @ApiOperation(value = "Add book to 'will read' or 'have read' list")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResponseEntity<?> willRead(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long bookId,
            @ApiParam(value = "will read('0'), have read('1')", allowableValues = "0,1") @RequestParam("type") Long type) {
        Book book = bookService.findByIdAndActiveTrueAndIsPublishedTrue(bookId);
        return ResponseEntity.ok(userBookService.save(book.getId(), type));
    }
}
