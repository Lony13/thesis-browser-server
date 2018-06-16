package com.koczy.kurek.mizera.thesisbrowser.entity;

public class Thesis {
    private long id;
    private String topic;
    private String author;
    private String link;

    public Thesis(long id, String topic, String author, String link) {
        this.id = id;
        this.topic = topic;
        this.author = author;
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
