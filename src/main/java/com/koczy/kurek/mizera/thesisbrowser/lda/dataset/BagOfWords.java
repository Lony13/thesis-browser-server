package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BagOfWords {
    private ThesisDao thesisDao;

    private final int numDocs;
    private final int numWords;

    public BagOfWords() {
        this.thesisDao = new ThesisDao();
        this.numDocs   = thesisDao.getNumDocs();

        int numWords = 0;
        for(int id=1; id<=numDocs; id++){
            Map<Integer, Integer> thesisBow = thesisDao.getThesisBow(id);
            for (Map.Entry<Integer, Integer> entry : thesisBow.entrySet()){
                numWords += entry.getValue();
            }
        }
        this.numWords  = numWords;
    }

    public int getNumDocs() {
        return numDocs;
    }

    public List<Integer> getWords(final int docID) {
        if (docID <= 0 || getNumDocs() < docID) {
            throw new IllegalArgumentException();
        }
        Map<Integer, Integer> thesisBow = thesisDao.getThesisBow(docID);

        List<Integer> words = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : thesisBow.entrySet()){
            for(int i=0; i < entry.getValue(); i++){
                words.add(entry.getKey());
            }
        }
        return words;
    }

    public int getNumWords() {
        return numWords;
    }
}
