package com.bookportal.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREATE_DATE")
    @JsonIgnore
    private Date createDate;

    @Column(name = "UPDATE_DATE")
    @JsonIgnore
    private Date updateDate;

    @Column(name = "ACTIVE")
    private boolean active;

    @Column(name = "OPERATION_TYPE")
    @JsonIgnore
    private String operationType;

    @PrePersist
    public void onPrePersist() {
        this.setOperationType("SAVE");
        this.setCreateDate(new Date());
        this.setUpdateDate(new Date());
        this.setActive(true);
    }

    @PreUpdate
    public void onPreUpdate() {
        this.setOperationType("UPDATE");
        this.setUpdateDate(new Date());
    }

    @PreRemove
    public void onPreRemove() {
        this.setOperationType("DELETE");
        this.setUpdateDate(new Date());
        this.setActive(false);
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", active=" + active +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}
