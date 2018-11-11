package com.koczy.kurek.mizera.thesisbrowser.model;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ThesisDetails extends ThesisResponse {

    private int citationNo;
    private Integer publicationDate;
    private List<String> relatedTheses;
    private Set<String> keyWords;

    public ThesisDetails(){}

    public ThesisDetails(int citationNo, Integer publicationDate, List<String> relatedTheses, Set<String> keyWords) {
        this.citationNo = citationNo;
        this.publicationDate = publicationDate;
        this.relatedTheses = relatedTheses;
        this.keyWords = keyWords;
    }

    public ThesisDetails(Thesis thesis) {
        super(thesis);
        this.citationNo = Optional.ofNullable(thesis.getCitationNo()).orElse(-1);
        this.publicationDate = thesis.getPublicationDate();
        this.relatedTheses = thesis.getRelatedTheses();
        this.keyWords = thesis.getKeyWords();
    }

    public ThesisDetails(int id, String title, String linkToPDF, List<String> authors, int citationNo,
                         Integer publicationDate, List<String> relatedTheses, Set<String> keyWords) {
        super(id, title, linkToPDF, authors);
        this.citationNo = citationNo;
        this.publicationDate = publicationDate;
        this.relatedTheses = relatedTheses;
        this.keyWords = keyWords;
    }

    public int getCitationNo() {
        return citationNo;
    }

    public void setCitationNo(int citationNo) {
        this.citationNo = citationNo;
    }

    public Integer getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Integer publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<String> getRelatedTheses() {
        return relatedTheses;
    }

    public void setRelatedTheses(List<String> relatedTheses) {
        this.relatedTheses = relatedTheses;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        this.keyWords = keyWords;
    }
}
