package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "USER")
@Data
public class User extends BaseEntity {

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "MAIL", unique = true, nullable = false)
    private String mail;

    @Column(name = "PASSWORD", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "SOCIAL")
    private boolean social = false;

    @Column(name = "SOCIAL_TYPE")
    private String socialType;

    @Column(name = "GOOGLE_ID", unique = true)
    private String googleId;

    @Column(name = "FACEBOOK_ID", unique = true)
    private String facebookId;

    @Column(name = "PP_URL")
    private String ppUrl;

    @ManyToMany(
            cascade = CascadeType.DETACH,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @JsonIgnore
    private List<Role> roles;

    @Override
    public String toString() {
        return "User{" +
                "mail='" + mail + '\'' +
                '}';
    }
}