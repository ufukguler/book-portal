package com.bookportal.api.util.mapper;

import com.bookportal.api.entity.Author;
import com.bookportal.api.model.AuthorDTO;
import com.bookportal.api.model.AuthorUpdateDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author authorDTOtoAuthor(AuthorDTO dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setAbout(dto.getAbout());
        author.setImageUrl(dto.getImageUrl());
        return author;
    }

    public Author authorUpdateDTOtoAuthor(AuthorUpdateDTO dto, Author author) {
        author.setName(dto.getName());
        author.setAbout(dto.getAbout());
        author.setImageUrl(dto.getImageUrl());
        return author;
    }
}
