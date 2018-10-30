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

package com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.internal;

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.VocabProbability;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabulary;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.Inference;
import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.LDA_NUM_ITERATION;
import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.LDA_SEED;

@Component
public class CollapsedGibbsSampler implements Inference {
    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());

    private LDA lda;
    private Topics topics;
    private Documents documents;
    private boolean ready = false;

    @Override
    public void setUp(LDA lda) {
        if(Objects.isNull(lda)){
            logger.warning( "Lda is null");
            return;
        }
        this.lda = lda;

        initialize(this.lda);
        initializeTopicAssignment(LDA_SEED);

        this.ready = true;
    }
    
    private void initialize(LDA lda) {
        this.topics = new Topics(lda);
        this.documents = new Documents(lda);
    }
    
    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        if (!ready) {
            logger.warning( "Instance has not set up yet");
            return;
        }

        for (int i = 1; i <= LDA_NUM_ITERATION; i++) {
            logger.info( "Collapsed gibbs sampler iteration: " + i);
            runSampling();
        }
    }

    void runSampling() {
        for (Document document : documents.getDocuments()) {
            for (int wordID = 0; wordID < document.getDocLength(); ++wordID) {
                final Topic oldTopic = topics.get(document.getTopicID(wordID));
                document.decrementTopicCount(oldTopic.getId());
                
                final Vocabulary vocabulary = document.getVocabulary(wordID);
                oldTopic.decrementVocabCount(vocabulary.id());
                
                IntegerDistribution distribution
                    = getFullConditionalDistribution(lda.getNumTopics(), document.getId(), vocabulary.id());
                
                final int newTopicID = distribution.sample();
                document.setTopicID(wordID, newTopicID);
                
                document.incrementTopicCount(newTopicID);
                final Topic newTopic = topics.get(newTopicID);
                newTopic.incrementVocabCount(vocabulary.id());
            }
        }
    }

    IntegerDistribution getFullConditionalDistribution(final int numTopics, final int docID, final int vocabID) {
        int[] topics = IntStream.range(0, numTopics).toArray();
        double[] probabilities = Arrays.stream(topics)
                                       .mapToDouble(t -> getTheta(docID, t) * getPhi(t, vocabID))
                                       .toArray();
        return new EnumeratedIntegerDistribution(topics, probabilities); 
    }

    void initializeTopicAssignment(final long seed) {
        documents.initializeTopicAssignment(topics, seed);
    }

    int getDTCount(final int docID, final int topicID) {
        if (!ready){
            logger.warning( "Instance has not set up yet");
            return -1;
        }
        if (docID <= 0 || lda.getBow().getNumDocs() < docID
                || topicID < 0 || lda.getNumTopics() <= topicID){
            logger.warning( "Given docId or topicId does not exists");
            return -1;
        }
        return documents.getTopicCount(docID, topicID);
    }

    int getTVCount(final int topicID, final int vocabID) {
        if (!ready){
            logger.warning( "Instance has not set up yet");
            return -1;
        }
        if (topicID < 0 || lda.getNumTopics() <= topicID || vocabID <= 0){
            logger.warning( "Given topicId or vocabId does not exists");
            return -1;
        }
        final Topic topic = topics.get(topicID);
        return topic.getVocabCount(vocabID);
    }

    int getTSumCount(final int topicID) {
        if (topicID < 0 || lda.getNumTopics() <= topicID){
            logger.warning( "Given topicId does not exists");
            return -1;
        }
        final Topic topic = topics.get(topicID);
        return topic.getSumCount();
    }

    @Override
    public double getTheta(final int docID, final int topicID) {
        if (!ready){
            logger.warning( "Instance has not set up yet");
            return -1;
        }
        return documents.getTheta(docID, topicID, lda.getAlpha(topicID), lda.getSumAlpha());
    }

    @Override
    public double getPhi(int topicID, int vocabID) {
        if (!ready){
            logger.warning( "Instance has not set up yet");
            return -1;
        }
        return topics.getPhi(topicID, vocabID, lda.getBeta());
    }
    
    @Override
    public List<VocabProbability> getVocabsSortedByPhi(int topicID) {
        return topics.getVocabsSortedByPhi(topicID, lda.getVocabularies(), lda.getBeta());
    }
}
