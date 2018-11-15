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

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.BagOfWords;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabularies;
import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Vocabulary;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Documents {
    private static final Logger logger = Logger.getLogger(Documents.class.getName());

    private List<Document> documents;
    
    Documents(LDA lda) {
        documents = new ArrayList<>();
        if(Objects.isNull(lda)){
            logger.warning( "Lda is not initialized");
            return;
        }
        for (int id = 1; id <= lda.getBow().getNumDocs(); id++) {
            List<Vocabulary> vocabList = getVocabularyList(id, lda.getBow(), lda.getVocabularies());
            Document doc = new Document(id, lda.getNumTopics(), vocabList);
            documents.add(doc);
        }
    }

    List<Vocabulary> getVocabularyList(int docID, BagOfWords bow, Vocabularies vocabs) {
        if(docID <= 0 || Objects.isNull(bow) || Objects.isNull(vocabs)){
            logger.warning( "Bag of words or vocabularies were not initialized");
            return Collections.emptyList();
        }
        return bow.getWords(docID).stream()
                                  .map(id -> vocabs.get(id))
                                  .collect(Collectors.toList());
    }

    List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    int getTopicCount(int docID, int topicID) {
        return documents.get(docID - 1).getTopicCount(topicID);
    }
    
    double getTheta(int docID, int topicID, double alpha, double sumAlpha) {
        if (docID <= 0 || documents.size() < docID){
            logger.warning( "There is no document with that id: " + docID);
            return -1;
        }
        return documents.get(docID - 1).getTheta(topicID, alpha, sumAlpha);
    }
    
    void initializeTopicAssignment(Topics topics, long seed) {
        for (Document document : getDocuments()) {
            document.initializeTopicAssignment(seed);
            for (int wordId = 0; wordId < document.getDocLength(); ++wordId) {
                final int topicID = document.getTopicID(wordId);
                final Topic topic = topics.get(topicID);
                final Vocabulary vocab = document.getVocabulary(wordId);
                topic.incrementVocabCount(vocab.id());
            }
        }
    }
}
