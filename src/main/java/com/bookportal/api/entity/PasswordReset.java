package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PASSWORD_RESET")
@Data
public class PasswordReset extends BaseEntity {

    @Column(name = "SECRET_KEY")
    private String secretKey;

    @Column(name = "VALIDITY")
    private Date validity;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Override
    public String toString() {
        return "PasswordReset{" +
                "secretKey='" + secretKey + '\'' +
                ", validity=" + validity +
                ", user=" + user +
                '}';
    }
}
