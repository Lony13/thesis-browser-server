package com.koczy.kurek.mizera.thesisbrowser.entity;

public class ThesisDetails extends Thesis {

    private long cititationNo;

    public ThesisDetails(String title, String author, String link, long cititationNo) {
        super(title, author, link);
        this.cititationNo = cititationNo;
    }

    public ThesisDetails(){}

    public long getCititationNo() {
        return cititationNo;
    }

    public void setCititationNo(long cititationNo) {
        this.cititationNo = cititationNo;
    }
}
