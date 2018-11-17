package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class BagOfWords {

    private static final Logger logger = Logger.getLogger(BagOfWords.class.getName());

    private ThesisBowManager thesisBowManager;

    private int numDocs;
    private int numWords;

    @Autowired
    public BagOfWords(ThesisBowManager thesisBowManager) {
        this.thesisBowManager = thesisBowManager;
    }

    public ThesisBowManager getThesisBowManager() {
        return thesisBowManager;
    }

    public void setupBow(){
        thesisBowManager.setupBowManager();
        this.numDocs   = thesisBowManager.getNumDocs();

        int numWords = 0;
        for(int id=1; id<=numDocs; id++){
            Map<Integer, Integer> thesisBow = thesisBowManager.getThesisBow(id);
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
            logger.warning("There is no document with given ID: " + docID);
            return Collections.emptyList();
        }
        Map<Integer, Integer> thesisBow = thesisBowManager.getThesisBow(docID);

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
