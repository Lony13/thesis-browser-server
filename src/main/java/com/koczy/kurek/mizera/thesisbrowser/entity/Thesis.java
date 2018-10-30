package com.koczy.kurek.mizera.thesisbrowser.entity;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Entity
public class Thesis {

    private static final Logger logger = Logger.getLogger(Thesis.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer thesisId;

    @NotNull
    private String title;
    private Integer citationNo;


    private String linkToPDF;
    private String pathToTXT;
    private Date publicationDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "relatedTheses", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "relatedTheses")
    private List<String> relatedTheses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "keyWords", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "keyWords")
    private Set<String> keyWords = new HashSet<>();

    @ManyToMany(mappedBy = "theses", fetch = FetchType.EAGER)
    private Set<Author> authors = new HashSet<>();


    public Thesis() {
    }

    public Thesis(String title, Set<Author> authors, String linkToPDF) {
        this.title = title;
        this.linkToPDF = linkToPDF;
        this.authors = authors;
    }

    public Thesis(String title, Set<Author> authors, String linkToPDF, Integer citationNo,
                  Date publicationDate, List<String> relatedTheses, Set<String> keyWords) {
        this.title = title;
        this.linkToPDF = linkToPDF;
        this.authors = authors;
        this.citationNo = citationNo;
        this.publicationDate = publicationDate;
        this.relatedTheses = relatedTheses;
        this.keyWords = keyWords;
    }

    @Deprecated
    public Thesis(String title, String authorName, String link) {
        this.title = title;
        this.linkToPDF = link;
        //TODO sort this out
        authors.add(new Author(authorName));
    }

    public Thesis(String title) {
        this.title = title;
    }

    //TODO remove; only for demo ThesisDemoService.searchTheses
    public String getAuthor() {
        logger.log(Level.WARNING, "Thesis.getAuthor() is deprecated. Use this method for demo purpose only.");
        return Objects.requireNonNull(authors.stream().findFirst().orElse(new Author())).toString();
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
