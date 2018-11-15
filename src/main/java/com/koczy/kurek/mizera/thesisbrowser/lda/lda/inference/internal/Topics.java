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
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabularies;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Topics {
    private static final Logger logger = Logger.getLogger(Topics.class.getName());

    private List<Topic> topics;
    
    Topics(LDA lda) {
        topics = new ArrayList<>();
        if(Objects.isNull(lda)){
            logger.warning("Lda is not initialized");
            return;
        }
        for (int id = 0; id < lda.getNumTopics(); id++) {
            topics.add(new Topic(id, lda.getVocabularies().size()));
        }
    }

    Topic get(int id) {
        if(id < 0 || id >= topics.size()) {
            logger.warning( "There is no topic with that id: " + id);
            return new Topic(-1,-1);
        }
        return topics.get(id);
    }

    double getPhi(int topicID, int vocabID, double beta) {
        if (topicID < 0 || topics.size() <= topicID) {
            logger.warning( "There is no topic with that id: " + topicID);
            return -1;
        }
        return topics.get(topicID).getPhi(vocabID, beta);
    }
    
    List<VocabProbability> getVocabsSortedByPhi(int topicID, Vocabularies vocabs, final double beta) {
        if (topicID < 0 || topics.size() <= topicID || Objects.isNull(vocabs) || beta <= 0.0)  {
            logger.warning( "Invalid topic id, beta value or vocabs have not been initialised, topicID: "
                    + topicID + ", beta: " + beta);
            return Collections.emptyList();
        }
        
        Topic topic = topics.get(topicID);
        List<VocabProbability> vocabProbPairs
            = vocabs.getVocabularyList()
                    .stream()
                    .map(v -> new VocabProbability(v.toString(), topic.getPhi(v.id(), beta)))
                    .sorted((p1, p2) -> Double.compare(p2.getProbability(), p1.getProbability()))
                    .collect(Collectors.toList());
        return Collections.unmodifiableList(vocabProbPairs);
    }
}
