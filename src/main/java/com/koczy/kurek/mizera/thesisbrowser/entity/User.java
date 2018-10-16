package com.koczy.kurek.mizera.thesisbrowser.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public class User {

    private long id;
    private String username;

    @JsonIgnore
    private String password;

    private long salary;

    private int age;

    private Set<Role> roles;

    public User(long id, String username, String password, long salary, int age, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salary = salary;
        this.age = age;
        this.roles = roles;
    }

    public User(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}