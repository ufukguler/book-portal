package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "BOOK")
@Data
public class Book extends BaseEntity {
    @Column(name = "NAME")
    private String name;

    @ManyToMany(
            cascade = CascadeType.DETACH,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "BOOK_AUTHOR",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID")
    )
    private List<Author> authors;

    @ManyToMany(
            cascade = CascadeType.DETACH,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "BOOK_CATEGORY",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    private List<Category> categories;

    @Column(name = "PAGE")
    private int page;

    @OneToOne
    @JoinColumn(name = "PUBLISHER_ID", referencedColumnName = "ID")
    private Publisher publisher;

    @Column(name = "YEAR")
    private int year;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User user;

    @Column(name = "TAGS")
    private String tag;

    /* panel üzerinden en son editleyen kullanıcı bilgisi editor admin olmalı */
    @OneToOne
    @JoinColumn(name = "EDITOR_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User editor;

    @Column(name = "IS_PUBLISHED")
    private Boolean isPublished;

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", page=" + page +
                ", year=" + year +
                ", imageUrl='" + imageUrl + '\'' +
                ", tag='" + tag + '\'' +
                ", isPublished=" + isPublished +
                '}';
    }
}
