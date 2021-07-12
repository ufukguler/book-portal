package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "QUOTE_FAVOURITE")
@Data
public class Favourite extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "QUOTE_ID", referencedColumnName = "ID")
    private Quote quote;

    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Override
    public String toString() {
        return "Favourite{" +
                "quote=" + quote +
                ", user=" + user +
                '}';
    }
}
