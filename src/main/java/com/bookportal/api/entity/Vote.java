package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "VOTE")
@Data
public class Vote extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name = "VOTE")
    private int vote;

    @Override
    public String toString() {
        return "Vote{" +
                "book=" + book +
                ", user=" + user +
                ", vote=" + vote +
                '}';
    }
}
