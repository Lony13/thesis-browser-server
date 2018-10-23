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

package com.koczy.kurek.mizera.thesisbrowser.lda.dataset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Dataset {
    private BagOfWords bow;
    private Vocabularies vocabs;

    @Autowired
    public Dataset(@Value("${lda.vocabs}") String vocabsFileName){
        vocabs = new Vocabularies(vocabsFileName);
        bow = new BagOfWords();
    }
    
    public BagOfWords getBow() {
        return bow;
    }

    public int getNumDocs() {
        return bow.getNumDocs();
    }

    public List<Integer> getWords(int docID) {
        return bow.getWords(docID);
    }

    public int getNumVocabs() {
        return vocabs.size();
    }

    public int getNumWords() {
        return bow.getNumWords();
    }
    
    public Vocabulary get(int id) {
        return vocabs.get(id);
    }
    
    public int size() {
        return vocabs.size();
    }
    
    public Vocabularies getVocabularies() {
        return vocabs;
    }
    
    public List<Vocabulary> getVocabularyList() {
        return vocabs.getVocabularyList();
    }
}
