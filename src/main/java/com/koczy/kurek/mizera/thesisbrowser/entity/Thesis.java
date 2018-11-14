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
    private Integer publicationDate;

    @ElementCollection
    @CollectionTable(name = "similarityVector", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "similarityVector")
    private List<Double> similarityVector = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bow", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "bow")
    private Map<Integer, Integer> bow = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "relatedTheses", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "relatedTheses")
    private List<String> relatedTheses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "keyWords", joinColumns = @JoinColumn(name = "thesisId"))
    @Column(name = "keyWords")
    private Set<String> keyWords = new HashSet<>();

    @ManyToMany(mappedBy = "theses", fetch = FetchType.EAGER)
    @OrderBy
    private Set<Author> authors = new HashSet<>();


    public Thesis() {
    }

    public Thesis(String title, Set<Author> authors, String linkToPDF) {
        this.title = title;
        this.linkToPDF = linkToPDF;
        this.authors = authors;
    }

    public Thesis(String title, String linkToPDF) {
        this.title = title;
        this.linkToPDF = linkToPDF;
    }

    public Thesis(String title, Set<Author> authors, String linkToPDF, Integer citationNo,
                  Integer publicationDate, List<String> relatedTheses, Set<String> keyWords) {
        this.title = title;
        this.linkToPDF = linkToPDF;
        this.authors = authors;
        this.citationNo = citationNo;
        this.publicationDate = publicationDate;
        this.relatedTheses = relatedTheses;
        this.keyWords = keyWords;
    }

    public Thesis(String title) {
        this.title = title;
    }

    //TODO remove; only for demo ThesisDemoService.searchTheses
    @Deprecated
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

    public Integer getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Integer publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<Double> getSimilarityVector() {
        return similarityVector;
    }

    public void setSimilarityVector(List<Double> similarityVector) {
        this.similarityVector = similarityVector;
    }

    public Map<Integer, Integer> getBow() {
        return bow;
    }

    public void setBow(Map<Integer, Integer> bow) {
        this.bow = bow;
    }

}
