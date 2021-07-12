package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "APP_REVIEW")
@Data
public class Review extends BaseEntity {
    @Column(name = "REVIEW")
    private String review;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Override
    public String toString() {
        return "Review{" +
                "comment='" + review + '\'' +
                ", user=" + user +
                '}';
    }
}
