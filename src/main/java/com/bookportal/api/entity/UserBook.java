package com.bookportal.api.entity;


import com.bookportal.api.model.enums.UserBookEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "USER_BOOK_READ")
@Data
public class UserBook extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @OneToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;

    @Column(name = "TYPE", nullable = false)
    private UserBookEnum type;

    @Override
    public String toString() {
        return "UserBook{" +
                "user=" + user.getMail() +
                ", book=" + book.getId() + '-' + book.getName() +
                ", type=" + type +
                '}';
    }
}
