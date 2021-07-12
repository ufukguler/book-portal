package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TOP20")
@Data
public class Top20 {
    @Id
    @Column(name = "BOOK_ID")
    private Long bookId;

    @Column(name = "AVERAGE")
    private double average;

    @Column(name = "WR")
    private double wr;
}
