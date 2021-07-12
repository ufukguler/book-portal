package com.bookportal.api.entity;

import com.bookportal.api.model.enums.HomePageEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "HOMEPAGE")
@Data
public class HomePage extends BaseEntity {

    @Column(name = "TYPE", nullable = false)
    private HomePageEnum type;

    @OneToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DESCRIPTION")
    private String description;

    @Override
    public String toString() {
        return "HomePage{" +
                "type=" + type +
                ", book=" + book +
                '}';
    }
}
