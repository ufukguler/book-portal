package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "QUOTE")
@Data
public class Quote extends BaseEntity {
    @Column(name = "QUOTE")
    private String quote;

    @OneToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name = "COUNT")
    private int count = 0;

    @Override
    public String toString() {
        return "Quote{" +
                "quote='" + quote + '}';
    }
}
