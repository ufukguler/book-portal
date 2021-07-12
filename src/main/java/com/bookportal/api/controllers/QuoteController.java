package com.bookportal.api.controllers;

import com.bookportal.api.model.QuoteDTO;
import com.bookportal.api.service.FavouriteService;
import com.bookportal.api.service.QuoteService;
import com.bookportal.api.util.mapper.QuoteMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/quote")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;
    private final FavouriteService favouriteService;

    @GetMapping
    @ApiOperation(value = "List quotes")
    public ResponseEntity<?> getQuotesByPageSize(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(quoteService.getByPageSize(page, size));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Save quote")
    public ResponseEntity<?> saveQuote(@Valid @RequestBody QuoteDTO dto) {
        return ResponseEntity.ok(quoteService.save(QuoteMapper.quoteDtoToQuote(dto), dto.getBookId()));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get quote by Id")
    public ResponseEntity<?> findQuoteById(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(quoteService.findByIdAndActiveTrue(id));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Delete quote")
    public ResponseEntity<?> deleteQuoteById(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(quoteService.delete(id));
    }

    @GetMapping("/random")
    @ApiOperation(value = "Get random quote")
    public ResponseEntity<?> getRandomQuote() {
        return ResponseEntity.ok(quoteService.getRandomQuote());
    }

    @GetMapping("/{id}/fav")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Add/delete quote to user's favourite list")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> addQuoteToFavourite(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(favouriteService.addQuoteToFavourite(id));
    }

    @GetMapping("/{id}/favourites")
    @ApiOperation(value = "Get user who added quote to their favourite list")
    public ResponseEntity<?> findFavorites(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(favouriteService.findFavouritesByQuote(id));
    }
}
