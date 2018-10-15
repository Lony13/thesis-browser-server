package com.koczy.kurek.mizera.thesisbrowser.entity;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.*;

@Entity
public class Thesis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thesisId;

    @NotNull
    private String title;
    private Integer citationNo;


    private String linkToPDF;
    private String pathToTXT;
    private Date publicationDate;

    @ElementCollection
    @CollectionTable(name = "relatedTheses", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "relatedTheses")
    private List<String> relatedTheses = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "keyWords", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "keyWords")
    private Set<String> keyWords = new HashSet<>();


    @ManyToMany(mappedBy = "theses")
    private Set<Author> authors = new HashSet<>();


    public Thesis() {
    }

    public Thesis(String title, Integer citationNo, Set<Author> authors) {
        this.title = title;
        this.citationNo = citationNo;
        this.authors = authors;
    }
    public Thesis(String title,String authorName, String link){
        this.title=title;
        this.linkToPDF=link;
        //TODO sort this out
        authors.add(new Author(authorName,authorName));
    }

    public Thesis(String title) {
        this.title = title;
    }

    //TODO remove; only for demo ThesisDemoService.searchTheses
    public String getAuthor(){
        return Objects.requireNonNull(authors.stream().findFirst().orElse(null)).toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCitationNo() {
        return citationNo;
    }

    public void setCitationNo(Integer citationNo) {
        this.citationNo = citationNo;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords = keyWords;
    }

    public void addKeyWord(String keyword) {
        keyWords.add(keyword);
    }

    public List<String> getRelatedTheses() {
        return relatedTheses;
    }

    public void setRelatedTheses(List<String> relatedTheses) {
        this.relatedTheses = relatedTheses;
    }

    public void addRelatedTheses(String thesisName) {
        this.relatedTheses.add(thesisName);
    }

    public Integer getThesisId() {
        return thesisId;
    }

    public void setThesisId(Integer thesisId) {
        this.thesisId = thesisId;
    }

    public String getLinkToPDF() {
        return linkToPDF;
    }

    public void setLinkToPDF(String linkToPDF) {
        this.linkToPDF = linkToPDF;
    }

    public String getPathToTXT() {
        return pathToTXT;
    }

    public void setPathToTXT(String pathToTXT) {
        this.pathToTXT = pathToTXT;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }
}
