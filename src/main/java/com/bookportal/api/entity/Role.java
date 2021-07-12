package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ROLE")
@Data
public class Role extends BaseEntity {
    @Column(name = "NAME", unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private List<User> users;

    @Override
    public String toString() {
        return "Role{name='" + name + '}';
    }
}