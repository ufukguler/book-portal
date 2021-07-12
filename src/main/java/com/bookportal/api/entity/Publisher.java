package com.bookportal.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PUBLISHER")
@Data
public class Publisher extends BaseEntity {
    @Column(name = "NAME", unique = true)
    private String name;

    @Override
    public String toString() {
        return "Publisher{" +
                "name='" + name + '\'' +
                '}';
    }
}
