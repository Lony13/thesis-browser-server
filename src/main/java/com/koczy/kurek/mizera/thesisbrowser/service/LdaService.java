package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Dataset;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import com.koczy.kurek.mizera.thesisbrowser.model.CompareThesesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.LDA_SIMILARITY_THRESHOLD;

@Service
public class LdaService implements ILdaService{

    private Dataset dataset;
    private LDA lda;
    private ThesisDAO thesisDao;

    @Autowired
    public LdaService(Dataset dataset, LDA lda, ThesisDAO thesisDao){
        this.dataset = dataset;
        this.lda = lda;
        this.thesisDao = thesisDao;
    }

    @Override
    public ResponseEntity run() {
        this.lda.run();
        lda.createSimilarityVectorForEveryThesis();
        return new ResponseEntity<>("LDA completed", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getSimilarity(int docID1, int docID2) {
        return new ResponseEntity<>(cosineSimilarity(this.thesisDao.getTopicSimilarityVector(docID1),
                                this.thesisDao.getTopicSimilarityVector(docID2)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Thesis>> getSimilarTheses(int id) {
        double[] thesisSimilarityVector = this.thesisDao.getTopicSimilarityVector(id);
        List<Thesis> similarTheses = new ArrayList<>();
        for(int thesisId : this.thesisDao.getThesesId()){
            if(isSimilarityAboveThreshold(thesisSimilarityVector, thesisId)){
                similarTheses.add(this.thesisDao.getThesis(thesisId));
            }
        }
        return new ResponseEntity<>(similarTheses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Integer>> getSimilarThesesAmong(CompareThesesDto compareThesesDto) {
        double[] thesisSimilarityVector = this.thesisDao.getTopicSimilarityVector(compareThesesDto.getExemplaryThesis());
        List<Integer> similarThesesId = new ArrayList<>();
        for(int thesisId : compareThesesDto.getThesesToCompare()){
            if(isSimilarityAboveThreshold(thesisSimilarityVector, thesisId)){
                similarThesesId.add(thesisId);
            }
        }
        return new ResponseEntity<>(similarThesesId, HttpStatus.OK);
    }

    private boolean isSimilarityAboveThreshold(double[] thesisSimilarityVector, int thesisId) {
        return cosineSimilarity(thesisSimilarityVector,
                this.thesisDao.getTopicSimilarityVector(thesisId))
                >= LDA_SIMILARITY_THRESHOLD;
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
