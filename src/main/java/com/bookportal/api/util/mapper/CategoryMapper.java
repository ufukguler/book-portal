package com.bookportal.api.util.mapper;


import com.bookportal.api.entity.Category;
import com.bookportal.api.model.CategoryDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public static Category dtoToCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategory(categoryDTO.getCategory());
        category.setActive(true);
        return category;
    }
}
