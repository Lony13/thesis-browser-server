package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ThesisBowManager {

    private ThesisDAO thesisDAO;

    List<Integer> databaseThesisId = new ArrayList<>();

    @Autowired
    public ThesisBowManager(ThesisDAO thesisDAO){
        this.thesisDAO = thesisDAO;
        this.databaseThesisId = this.thesisDAO.getThesisId();
    }

    public void setupBowManager(){
        this.databaseThesisId = this.thesisDAO.getThesisId();
    }

    public int getNumDocs(){
        return this.thesisDAO.getNumDocs();
    }

    public Map<Integer, Integer> getThesisBow(int BowId){
        return thesisDAO.getThesisBow(databaseThesisId.get(BowId-1));
    }

    public void saveSimilarityVector(int bowID, double[] similarityVector){
        thesisDAO.saveSimilarityVector(databaseThesisId.get(bowID-1), similarityVector);
    }

}
