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

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.BagOfWords;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Dataset;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabularies;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.Inference;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LDA {
    private Hyperparameters hyperparameters;
    private final int numTopics;
    private Dataset dataset;
    private final Inference inference;
    private boolean trained;

    @Autowired
    public LDA(Inference inference, Dataset dataset, Hyperparameters hyperparameters, @Value("${lda.numTopics}") int numTopics) {
        this.hyperparameters = hyperparameters;
        this.numTopics       = numTopics;
        this.dataset         = dataset;
        this.inference       = inference;
        this.trained         = false;
    }

    /**
     * Get the vocabulary from its ID.
     * @param vocabID
     * @return the vocabulary
     * @throws IllegalArgumentException vocabID <= 0 || the number of vocabularies < vocabID
     */
    public String getVocab(int vocabID) {
        if (vocabID <= 0 || dataset.getNumVocabs() < vocabID) {
            throw new IllegalArgumentException();
        }
        return dataset.get(vocabID).toString();
    }

    /**
     * Run model inference.
     */
    public void run() {
        inference.setUp(this);
        inference.run();
        trained = true;
    }
    
    /**
     * Get hyperparameter alpha corresponding to topic.
     * @param topic
     * @return alpha corresponding to topicID
     * @throws ArrayIndexOutOfBoundsException topic < 0 || #topics <= topic
     */
    public double getAlpha(final int topic) {
        if (topic < 0 || numTopics <= topic) {
            throw new ArrayIndexOutOfBoundsException(topic);
        }
        return hyperparameters.alpha(topic);
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

    /**
     * Get the value of doc-topic probability \theta_{docID, topicID}.
     * @param docID
     * @param topicID
     * @return the value of doc-topic probability
     * @throws IllegalArgumentException docID <= 0 || #docs < docID || topicID < 0 || #topics <= topicID
     * @throws IllegalStateException call this method when the inference has not been finished yet
     */
    public double getTheta(final int docID, final int topicID) {
        if (docID <= 0 || dataset.getNumDocs() < docID
                || topicID < 0 || numTopics <= topicID) {
            throw new IllegalArgumentException();
        }
        if (!trained) {
            throw new IllegalStateException();
        }

        return inference.getTheta(docID, topicID);
    }

    /**
     * Get the value of topic-vocab probability \phi_{topicID, vocabID}.
     * @param topicID
     * @param vocabID
     * @return the value of topic-vocab probability
     * @throws IllegalArgumentException topicID < 0 || #topics <= topicID || vocabID <= 0
     * @throws IllegalStateException call this method when the inference has not been finished yet
     */
    public double getPhi(final int topicID, final int vocabID) {
        if (topicID < 0 || numTopics <= topicID || vocabID <= 0) {
            throw new IllegalArgumentException();
        }
        if (!trained) {
            throw new IllegalStateException();
        }

        return inference.getPhi(topicID, vocabID);
    }
    
    public Vocabularies getVocabularies() {
        return dataset.getVocabularies();
    }
    
    public List<Pair<String, Double>> getVocabsSortedByPhi(int topicID) {
        return inference.getVocabsSortedByPhi(topicID);
    }
    
    /**
     * Compute the perplexity of trained LDA for the test bag-of-words dataset.
     * @param testDataset
     * @return the perplexity for the test bag-of-words dataset
     */
    public double computePerplexity(Dataset testDataset) {
        double loglikelihood = 0.0;
        for (int d = 1; d <= testDataset.getNumDocs(); ++d) {
            for (Integer w : testDataset.getWords(d)) {
                double sum = 0.0;
                for (int t = 0; t < getNumTopics(); ++t) {
                     sum += getTheta(d, t) * getPhi(t, w.intValue()); 
                }
                loglikelihood += Math.log(sum);
            }
        }
        return Math.exp(-loglikelihood / testDataset.getNumWords());
    }

    public double similarity(final int docID1, final int docID2){
        return cosineSimilarity(getTopicSimilarityVector(docID1), getTopicSimilarityVector(docID2));
    }

    public double[] getTopicSimilarityVector(final int docID){
        double[] topicSimilarityVec = new double[numTopics];
        for(int topicNum=0; topicNum<numTopics; topicNum++){
            topicSimilarityVec[topicNum] = getTheta(docID,topicNum);
        }
        return topicSimilarityVec;
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
