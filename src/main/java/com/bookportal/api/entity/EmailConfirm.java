package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "EMAIL_CONFIRM")
@Data
public class EmailConfirm extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name = "SECRET_KEY")
    private String secretKey;


    @Override
    public String toString() {
        return "Favourite{" +
                "secretKey=" + secretKey +
                ", user=" + user +
                '}';
    }
}
