package com.bookportal.api.service;

import com.bookportal.api.entity.Category;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CategoryServiceTest {

    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    CategoryService categoryService;

    @Test
    void getByPageSize() {
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = new PageImpl<>(Collections.singletonList(new Category()));
        Mockito.lenient()
                .when(categoryRepository.findAllByActiveTrue(pageable))
                .thenReturn(categoryPage);
        Page<Category> byPageSize = categoryService.getByPageSize(page, size);
        Assertions.assertTrue(byPageSize.getSize() > 0);
        assertFalse(byPageSize.isEmpty());
    }

    @Test
    void findById() {
        Long id = 1L;
        Category category = new Category();
        category.setCategory("test");
        Mockito.lenient()
                .when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));
        Category byId = categoryService.findById(id);
        assertEquals(category.getCategory(), byId.getCategory());

    }

    @Test
    void findById_CustomNotFoundException() {
        Long id = 1L;
        Category category = new Category();
        category.setCategory("test");
        Mockito.lenient()
                .when(categoryRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(CustomNotFoundException.class, () -> categoryService.findById(id));

    }

    @Test
    void findById_throws_404() {
        Long id = 1L;
        Mockito.lenient()
                .when(categoryRepository.findById(id))
                .thenThrow(CustomNotFoundException.class);
        assertThrows(CustomNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void save() {
        Category category = new Category();
        category.setCategory("test");

        Mockito.lenient()
                .when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);
        Mockito.lenient()
                .when(categoryRepository.findByCategory(any()))
                .thenReturn(Optional.empty());
        Category save = categoryService.save(category);
        assertEquals(category.getCategory(), save.getCategory());
    }

    @Test
    void save_alreadyExist_throwsError() {
        Category category = new Category();
        category.setCategory("test");

        Mockito.lenient()
                .when(categoryRepository.findByCategory(any()))
                .thenReturn(Optional.of(category));
        assertThrows(CustomAlreadyExistException.class, () -> categoryService.save(category));
    }
}