package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "AUTHOR")
@Data
public class Author extends BaseEntity {
    @Column(name = "NAME")
    private String name;

    @Column(name = "ABOUT")
    private String about;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @ManyToMany(mappedBy = "authors")
    @JsonBackReference
    private List<Book> books;

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                '}';
    }
}
