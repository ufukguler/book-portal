package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Category;
import com.bookportal.api.model.CategoryDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    @Test
    void dtoToCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory("test");

        Category category = CategoryMapper.dtoToCategory(dto);
        assertEquals(dto.getCategory(), category.getCategory());
    }
}