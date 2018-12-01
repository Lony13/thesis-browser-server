package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import com.koczy.kurek.mizera.thesisbrowser.model.CompareThesesDto;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.LDA_SIMILARITY_THRESHOLD;

@Service
public class LdaService implements ILdaService{

    private LDA lda;
    private IThesisDao thesisDao;

    @Autowired
    public LdaService(LDA lda, IThesisDao thesisDao){
        this.lda = lda;
        this.thesisDao = thesisDao;
    }

    @Override
    public ResponseEntity<ServerInfo> run() {
        this.lda.run();
        this.lda.createSimilarityVectorForEveryThesis();
        return new ResponseEntity<>(new ServerInfo(new Date(),
                "LDA completed for " + lda.getBow().getNumDocs()+ " papers"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getSimilarity(int docID1, int docID2) {
        return new ResponseEntity<>(cosineSimilarity(this.thesisDao.getTopicSimilarityVector(docID1),
                                this.thesisDao.getTopicSimilarityVector(docID2)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ThesisResponse>> getSimilarTheses(int id) {
        double[] thesisSimilarityVector = this.thesisDao.getTopicSimilarityVector(id);
        List<Thesis> similarTheses = new ArrayList<>();
        for(int thesisId : this.thesisDao.getThesesId()){
            if(isSimilarityAboveThreshold(thesisSimilarityVector, thesisId)){
                similarTheses.add(this.thesisDao.getThesis(thesisId));
            }
        }
        return new ResponseEntity<>(similarTheses.stream()
                .map(ThesisResponse::new)
                .collect(Collectors.toList()), HttpStatus.OK);
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

    @Override
    public ResponseEntity<List<ThesisResponse>> getSimilarThesesFromFilter(ThesisFilters thesisFilters) {
        List<Thesis> thesesFromFilterWithSimVector = thesisDao.searchTheses(thesisFilters)
                .stream()
                .filter(thesis -> thesis.getSimilarityVector().size() > 0)
                .collect(Collectors.toList());

        List<Integer> exemplaryThesesWithSimVector = thesisFilters
                .getExemplaryTheses()
                .stream()
                .filter(thesisId -> thesisDao.getThesis(thesisId).getSimilarityVector().size() > 0)
                .collect(Collectors.toList());

        List<Thesis> similarTheses = thesisFilters
                .getExemplaryTheses()
                .stream()
                .map(thesisId -> thesisDao.getThesis(thesisId))
                .collect(Collectors.toList());

        if(Objects.isNull(similarTheses)){
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }

        for (Integer exemplaryThesisWithSimVector : exemplaryThesesWithSimVector) {
            for(Iterator<Thesis> it = thesesFromFilterWithSimVector.iterator(); it.hasNext();){
                Thesis thesisFromFilterWithSimVector = it.next();
                if(isSimilarityAboveThreshold(thesisDao.convertToPrimitives(thesisFromFilterWithSimVector.getSimilarityVector())
                        , exemplaryThesisWithSimVector)){
                    similarTheses.add(thesisFromFilterWithSimVector);
                    it.remove();
                }
            }
        }
        return new ResponseEntity<>(new ArrayList<>(similarTheses.stream()
                                                .map(ThesisResponse::new)
                                                .collect(Collectors.toSet())), HttpStatus.OK);
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
