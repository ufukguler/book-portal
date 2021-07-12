package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CATEGORY")
@Data
public class Category extends BaseEntity{
    @Column(name = "CATEGORY", unique = true)
    private String category;
}
