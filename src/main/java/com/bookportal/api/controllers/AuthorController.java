package com.bookportal.api.controllers;

import com.bookportal.api.entity.Author;
import com.bookportal.api.model.AuthorDTO;
import com.bookportal.api.model.AuthorUpdateDTO;
import com.bookportal.api.service.AuthorService;
import com.bookportal.api.util.mapper.AuthorMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;
    private final AuthorMapper authorMapper;

    @GetMapping
    @ApiOperation(value = "List authors")
    public Page<Author> getAuthorsByPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return authorService.getAllByPagination(page, size);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get author by Id")
    public Author getAuthorById(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return authorService.findByIdAndActiveTrue(id);
    }

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Save author")
    public ResponseEntity<?> saveAuthor(@Valid @RequestBody AuthorDTO authorDTO) {
        return new ResponseEntity<>(authorService.saveAuthor(authorMapper.authorDTOtoAuthor(authorDTO)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/update")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ApiOperation(value = "Update author")
    public Author updateAuthor(
            @ApiParam(defaultValue = "1") @PathVariable("id") Long id,
            @Valid @RequestBody AuthorUpdateDTO authorUpdateDTO) {
        return authorService.saveAuthor(authorMapper      // author service findById içerisinde kontrol ediliyor
                .authorUpdateDTOtoAuthor(authorUpdateDTO, authorService.findByIdAndActiveTrue(id)));
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Delete author")
    public Author deleteAuthor(
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return authorService.deleteAuthor(id);
    }

}
