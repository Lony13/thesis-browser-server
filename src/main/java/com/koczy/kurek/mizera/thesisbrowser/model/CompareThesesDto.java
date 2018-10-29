package com.koczy.kurek.mizera.thesisbrowser.model;

import java.util.List;

public class CompareThesesDto {

    private int exemplaryThesis;
    private List<Integer> thesesToCompare;

    public void setExemplaryThesis(int exemplaryThesis) {
        this.exemplaryThesis = exemplaryThesis;
    }

    public void setThesesToCompare(List<Integer> thesesToCompare) {
        this.thesesToCompare = thesesToCompare;
    }

    public int getExemplaryThesis() {

        return exemplaryThesis;
    }

    public List<Integer> getThesesToCompare() {
        return thesesToCompare;
    }
}
