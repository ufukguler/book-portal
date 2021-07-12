package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "NOTIFICATION")
@Data
public class Notification extends BaseEntity {
    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "COMMENT")
    private String comment;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Override
    public String toString() {
        return "Notification{" +
                "subject='" + subject + '\'' +
                ", comment='" + comment + '\'' +
                ", user=" + user +
                '}';
    }
}
