package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

public class VocabProbability {
    private String vocab;
    private Double probability;

    public VocabProbability(String vocab, Double probability) {
        this.vocab = vocab;
        this.probability = probability;
    }

    public String getVocab() {
        return vocab;
    }

    public Double getProbability() {
        return probability;
    }

    public void setVocab(String vocab) {
        this.vocab = vocab;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }
}
