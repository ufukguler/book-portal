package com.bookportal.api.controllers;

import com.bookportal.api.entity.Category;
import com.bookportal.api.model.CategoryDTO;
import com.bookportal.api.service.BookService;
import com.bookportal.api.service.CategoryService;
import com.bookportal.api.util.mapper.CategoryMapper;
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
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    @ApiOperation(value = "List categories")
    public ResponseEntity<?> getCategoriesByPagination(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(categoryService.getByPageSize(page, size));
    }

    @PostMapping
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @ApiOperation(value = "Save category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> save(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = CategoryMapper.dtoToCategory(categoryDTO);
        return ResponseEntity.ok(categoryService.save(category));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get category by Id")
    public ResponseEntity<?> findById(@ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping("/{id}/books")
    @ApiOperation(value = "Get books by category")
    public ResponseEntity<?> getBookByCategories(
            @ApiParam(defaultValue = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @ApiParam(defaultValue = "5") @RequestParam(name = "size", defaultValue = "5") int size,
            @ApiParam(defaultValue = "1", required = true) @PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.findByCategoryAndPageSize(page, size, id));
    }
}
