package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Author;
import com.bookportal.api.model.AuthorDTO;
import com.bookportal.api.model.AuthorUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = AuthorMapper.class)
class AuthorMapperTest {

    @Autowired
    AuthorMapper authorMapper;

    @Test
    void authorDTOtoAuthor() {
        AuthorDTO dto = new AuthorDTO();
        dto.setName("author");
        dto.setAbout("about");

        Author author = authorMapper.authorDTOtoAuthor(dto);

        assertEquals(dto.getName(), author.getName());
        assertEquals(dto.getAbout(), author.getAbout());
    }


    @Test
    void authorDTOtoAuthor_emptyUrl() {
        AuthorDTO dto = new AuthorDTO();
        dto.setName("author");
        dto.setAbout("about");

        Author author = authorMapper.authorDTOtoAuthor(dto);

        assertEquals(dto.getName(), author.getName());
        assertEquals(dto.getAbout(), author.getAbout());
    }

    @Test
    void authorUpdateDTOtoAuthor() {
        AuthorUpdateDTO dto = new AuthorUpdateDTO();
        dto.setName("author after");
        dto.setAbout("about");

        Author authorMapped = authorMapper.authorUpdateDTOtoAuthor(dto, new Author());

        assertEquals(dto.getName(), authorMapped.getName());
        assertEquals(dto.getAbout(), authorMapped.getAbout());
    }

    @Test
    void authorUpdateDTOtoAuthor_emptyUrl() {
        AuthorUpdateDTO dto = new AuthorUpdateDTO();
        dto.setName("author after");
        dto.setAbout("about");

        Author authorMapped = authorMapper.authorUpdateDTOtoAuthor(dto, new Author());

        assertEquals(dto.getName(), authorMapped.getName());
        assertEquals(dto.getAbout(), authorMapped.getAbout());
    }
}