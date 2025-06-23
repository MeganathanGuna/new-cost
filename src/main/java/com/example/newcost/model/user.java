package com.example.newcost.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "email", unique = true)
    private String email;
    private String password;
    private String name;

    protected user() {
        // For JPA
    }

    public user(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
