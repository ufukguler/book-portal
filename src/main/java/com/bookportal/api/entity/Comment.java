package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "BOOK_COMMENT")
@Data
public class Comment extends BaseEntity {
    @Column(name = "COMMENT", length = 999)
    private String comment;

    @OneToOne
    @JoinColumn(name = "BOOK_ID", referencedColumnName = "ID")
    private Book book;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User user;

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user=" + user +
                '}';
    }
}
