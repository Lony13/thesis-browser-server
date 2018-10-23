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

import java.util.List;

public class Document {
    private final int id;
    private TopicCounter topicCount;
    private TopicAssignment assignment;
    private Words words;


    Document(int id, int numTopics, List<Vocabulary> words) {
        if (id <= 0 || numTopics <= 0) throw new IllegalArgumentException();
        this.id = id;
        this.topicCount = new TopicCounter(numTopics);
        this.words = new Words(words);
        this.assignment = new TopicAssignment();
    }

    int id() {
        return id;
    }
    
    int getTopicCount(int topicID) {
        return topicCount.getTopicCount(topicID);
    }

    int getDocLength() {
        return words.getNumWords();
    }
    
    void incrementTopicCount(int topicID) {
        topicCount.incrementTopicCount(topicID);
    }
    
    void decrementTopicCount(int topicID) {
        topicCount.decrementTopicCount(topicID);
    }
    
    void initializeTopicAssignment(long seed) {
        assignment.initialize(getDocLength(), topicCount.size(), seed);
        for (int w = 0; w < getDocLength(); ++w) {
            incrementTopicCount(assignment.get(w));
        }
    }
    
    int getTopicID(int wordID) {
        return assignment.get(wordID);
    }
    
    void setTopicID(int wordID, int topicID) {
        assignment.set(wordID, topicID);
    }

    Vocabulary getVocabulary(int wordID) {
        return words.get(wordID);
    }
    
    double getTheta(int topicID, double alpha, double sumAlpha) {
        if (topicID < 0 || alpha <= 0.0 || sumAlpha <= 0.0) {
            throw new IllegalArgumentException();
        }
        return (getTopicCount(topicID) + alpha) / (getDocLength() + sumAlpha);
    }
}
