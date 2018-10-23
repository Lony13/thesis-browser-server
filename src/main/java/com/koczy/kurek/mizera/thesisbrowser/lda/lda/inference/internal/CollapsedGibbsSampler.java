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

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabulary;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.Inference;
import com.koczy.kurek.mizera.thesisbrowser.service.DownloadService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
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
    private boolean ready;

    @Autowired
    public CollapsedGibbsSampler() {
        this.ready = false;
    }
    
    @Override
    public void setUp(LDA lda) {
        this.lda = lda;

        initialize(this.lda);
        initializeTopicAssignment(LDA_SEED);

        this.ready = true;
    }
    
    private void initialize(LDA lda) {
        assert lda != null;
        this.topics = new Topics(lda);
        this.documents = new Documents(lda);
    }
    
    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        if (!ready) {
            throw new IllegalStateException("instance has not set up yet");
        }

        for (int i = 1; i <= LDA_NUM_ITERATION; ++i) {
            logger.log(Level.INFO, "Iteration: " + i);
            runSampling();
        }
    }

    void runSampling() {
        for (Document d : documents.getDocuments()) {
            for (int w = 0; w < d.getDocLength(); ++w) {
                final Topic oldTopic = topics.get(d.getTopicID(w));
                d.decrementTopicCount(oldTopic.id());
                
                final Vocabulary v = d.getVocabulary(w);
                oldTopic.decrementVocabCount(v.id());
                
                IntegerDistribution distribution
                    = getFullConditionalDistribution(lda.getNumTopics(), d.id(), v.id());
                
                final int newTopicID = distribution.sample();
                d.setTopicID(w, newTopicID);
                
                d.incrementTopicCount(newTopicID);
                final Topic newTopic = topics.get(newTopicID);
                newTopic.incrementVocabCount(v.id());
            }
        }
    }

    IntegerDistribution getFullConditionalDistribution(final int numTopics, final int docID, final int vocabID) {
        int[]    topics        = IntStream.range(0, numTopics).toArray();
        double[] probabilities = Arrays.stream(topics)
                                       .mapToDouble(t -> getTheta(docID, t) * getPhi(t, vocabID))
                                       .toArray();
        return new EnumeratedIntegerDistribution(topics, probabilities); 
    }

    void initializeTopicAssignment(final long seed) {
        documents.initializeTopicAssignment(topics, seed);
    }

    int getDTCount(final int docID, final int topicID) {
        if (!ready) throw new IllegalStateException();
        if (docID <= 0 || lda.getBow().getNumDocs() < docID
                || topicID < 0 || lda.getNumTopics() <= topicID) {
            throw new IllegalArgumentException();
        }
        return documents.getTopicCount(docID, topicID);
    }

    int getTVCount(final int topicID, final int vocabID) {
        if (!ready) throw new IllegalStateException();
        if (topicID < 0 || lda.getNumTopics() <= topicID || vocabID <= 0) {
            throw new IllegalArgumentException();
        }
        final Topic topic = topics.get(topicID);
        return topic.getVocabCount(vocabID);
    }

    int getTSumCount(final int topicID) {
        if (topicID < 0 || lda.getNumTopics() <= topicID) {
            throw new IllegalArgumentException();
        }
        final Topic topic = topics.get(topicID);
        return topic.getSumCount();
    }

    @Override
    public double getTheta(final int docID, final int topicID) {
        if (!ready) throw new IllegalStateException();
        return documents.getTheta(docID, topicID, lda.getAlpha(topicID), lda.getSumAlpha());
    }

    @Override
    public double getPhi(int topicID, int vocabID) {
        if (!ready) throw new IllegalStateException();
        return topics.getPhi(topicID, vocabID, lda.getBeta());
    }
    
    @Override
    public List<Pair<String, Double>> getVocabsSortedByPhi(int topicID) {
        return topics.getVocabsSortedByPhi(topicID, lda.getVocabularies(), lda.getBeta());
    }
}
