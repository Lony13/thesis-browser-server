/*
* Copyright 2015 Kohei Yamamoto
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.koczy.kurek.mizera.thesisbrowser.lda.lda;

import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IAuthorDao;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.*;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.Inference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Component
public class LDA {
    private static final Logger logger = Logger.getLogger(LDA.class.getName());

    private Hyperparameters hyperparameters;
    private int numTopics;
    private Dataset dataset;
    private final Inference inference;
    private boolean trained;
    private IAuthorDao authorDao;

    @Autowired
    public LDA(Inference inference,
               Dataset dataset,
               Hyperparameters hyperparameters,
               @Value("${lda.numTopics}") int numTopics,
               IAuthorDao authorDao) {
        this.authorDao       = authorDao;
        this.hyperparameters = hyperparameters;
        this.numTopics       = (numTopics == 0)?authorDao.getAuthorsNum():numTopics;
        this.dataset         = dataset;
        this.inference       = inference;
        this.trained         = false;
    }

    public String getVocab(int vocabID) {
        if(vocabID <= 0 || dataset.getNumVocabs() < vocabID){
            logger.warning("There is no vocab with that id: " + vocabID);
            return "";
        }
        return dataset.get(vocabID).toString();
    }

    public void run() {
        this.numTopics = (numTopics == 0)?authorDao.getAuthorsNum():numTopics;
        dataset.getBow().setupBow();
        inference.setUp(this);
        inference.run();
        trained = true;
    }

    public double getAlpha(final int topic) {
        return (topic < 0 || numTopics <= topic) ? -1 : hyperparameters.alpha(topic);
    }
    
    public double getSumAlpha() {
        return hyperparameters.sumAlpha();
    }

    public double getBeta() {
        return hyperparameters.beta();
    }

    public int getNumTopics() {
        return numTopics;
    }

    public BagOfWords getBow() {
        return dataset.getBow();
    }

    public double getTheta(final int docID, final int topicID) {
        if(!trained) {
            logger.warning("Lda is not trained");
            return -1;
        }
        if (docID <= 0 || dataset.getNumDocs() < docID
                || topicID < 0 || numTopics <= topicID){
            logger.warning("There is no such docId or topicId: " + topicID);
            return -1;
        }
        return inference.getTheta(docID, topicID);
    }

    public double getPhi(final int topicID, final int vocabID) {
        if(!trained) {
            logger.warning("Lda is not trained");
            return -1;
        }
        if (topicID < 0 || numTopics <= topicID || vocabID <= 0){
            logger.warning("There is no such topicId: " + topicID + " or vocabId: " + vocabID);
            return -1;
        }
        return inference.getPhi(topicID, vocabID);
    }
    
    public Vocabularies getVocabularies() {
        return dataset.getVocabularies();
    }
    
    public List<VocabProbability> getVocabsSortedByPhi(int topicID) {
        if (topicID < 0 || topicID >= this.numTopics){
            logger.warning("Invalid topicId: " + topicID);
            return Collections.emptyList();
        }
        return inference.getVocabsSortedByPhi(topicID);
    }

    public double computePerplexity(Dataset testDataset) {
        double loglikelihood = 0.0;
        for (int docId = 1; docId <= testDataset.getNumDocs(); docId++) {
            for (Integer wordNum : testDataset.getWords(docId)) {
                double sum = 0.0;
                for (int topicNum = 0; topicNum < getNumTopics(); topicNum++) {
                     sum += getTheta(docId, topicNum) * getPhi(topicNum, wordNum);
                }
                loglikelihood += Math.log(sum);
            }
        }
        return Math.exp(-loglikelihood / testDataset.getNumWords());
    }

    public double[] getTopicSimilarityVector(final int docID){
        double[] topicSimilarityVec = new double[numTopics];
        for(int topicNum=0; topicNum<numTopics; topicNum++){
            topicSimilarityVec[topicNum] = getTheta(docID,topicNum);
        }
        return topicSimilarityVec;
    }

    public void createSimilarityVectorForEveryThesis() {
        ThesisBowManager thesisBowManager = dataset.getBow().getThesisBowManager();
        for(int docID = 1; docID <= dataset.getBow().getNumDocs(); docID++){
            thesisBowManager.saveSimilarityVector(docID, getTopicSimilarityVector(docID));
        }
    }
}
