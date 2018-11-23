package com.koczy.kurek.mizera.thesisbrowser.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.text.WordUtils;

import javax.persistence.*;
import java.util.*;

@Entity
public class Author {

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "author_thesis",
            joinColumns = {@JoinColumn(name = "authorId")},
            inverseJoinColumns = {@JoinColumn(name = "thesisId")}
    )
    List<Thesis> theses = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorId;
    @NotNull
    private String name;
    private Date birthDate;
    private String academicTitle;
    private String university;

    public Author() {
    }

    public Author(String name, Date birthDate, String academicTitle, String university) {
        this.name = WordUtils.capitalize(name);
        this.birthDate = birthDate;
        this.academicTitle = academicTitle;
        this.university = university;
    }

    public Author(String name) {
        this.name = WordUtils.capitalize(name);
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public List<Thesis> getTheses() {
        return theses;
    }

    public void setTheses(ArrayList<Thesis> theses) {
        this.theses = theses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
