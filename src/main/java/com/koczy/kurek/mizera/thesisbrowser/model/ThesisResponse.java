package com.koczy.kurek.mizera.thesisbrowser.model;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;

import java.util.List;
import java.util.stream.Collectors;

public class ThesisResponse {
    private Integer id;
    private String title;
    private String linkToPDF;
    private List<String> authors;

    public ThesisResponse() {
    }

    public ThesisResponse(Thesis thesis) {
        this.id = thesis.getThesisId();
        this.title = thesis.getTitle();
        this.linkToPDF = thesis.getLinkToPDF();
        this.authors = thesis.getAuthors()
                .stream()
                .map(Author::getName)
                .collect(Collectors.toList());
    }

    public ThesisResponse(Integer id, String title, String linkToPDF, List<String> authors) {
        this.id = id;
        this.title = title;
        this.linkToPDF = linkToPDF;
        this.authors = authors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkToPDF() {
        return linkToPDF;
    }

    public void setLinkToPDF(String linkToPDF) {
        this.linkToPDF = linkToPDF;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
