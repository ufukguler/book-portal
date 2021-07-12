package com.bookportal.api.model;

import com.bookportal.api.entity.Book;
import lombok.Data;

@Data
public class Top20Response {
    private Book book;
    private double average;
    private double wr;
}
