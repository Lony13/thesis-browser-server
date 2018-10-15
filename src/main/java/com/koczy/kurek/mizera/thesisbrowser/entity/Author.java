package com.koczy.kurek.mizera.thesisbrowser.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Author {
    //author is on the owning side
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "author_thesis", // name of link table
            joinColumns = {@JoinColumn(name = "authorId")},
            inverseJoinColumns = {@JoinColumn(name = "thesisId")}
    )
    Set<Thesis> theses = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorId;
    @NotNull
    private String firstName;
    private String secondName;
    @NotNull
    private String lastName;
    private Date birthDate;
    private String academicTitle;
    private String university;

    public Author() {
    }

    public Author(String firstName, String secondName, String lastName, Date birthDate, String academicTitle, String university) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.academicTitle = academicTitle;
        this.university = university;
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Set<Thesis> getTheses() {
        return theses;
    }

    public void setTheses(Set<Thesis> theses) {
        this.theses = theses;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void addThesis(Thesis thesis) {
        theses.add(thesis);
    }
}
