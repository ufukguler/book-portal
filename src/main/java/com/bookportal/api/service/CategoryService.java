package com.bookportal.api.service;

import com.bookportal.api.entity.Category;
import com.bookportal.api.model.enums.ExceptionItemsEnum;
import com.bookportal.api.exception.CustomAlreadyExistException;
import com.bookportal.api.exception.CustomNotFoundException;
import com.bookportal.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<Category> getByPageSize(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAllByActiveTrue(pageable);
    }

    public Category findById(Long id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new
                CustomNotFoundException(ExceptionItemsEnum.CATEGORY.getValue());
    }

    public Category save(Category category) {
        if (isExist(category.getCategory())) {
            throw new CustomAlreadyExistException(ExceptionItemsEnum.CATEGORY.getValue());
        }
        category.setCategory(firstLetterUppercase(category.getCategory()));
        return categoryRepository.save(category);
    }

    private boolean isExist(String category) {
        return categoryRepository.findByCategory(category).isPresent();
    }

    private String firstLetterUppercase(String category) {
        String str = category.trim();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
