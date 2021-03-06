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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

class Words {
    private static final Logger logger = Logger.getLogger(Words.class.getName());

    private List<Vocabulary> words;
    
    Words(List<Vocabulary> words) {
        this.words = new ArrayList<>();
        if (Objects.isNull(words)){
            logger.warning( "Words were not initialized");
            return;
        }
        this.words = words; 
    }

    int getNumWords() {
        return words.size();
    }
    
    Vocabulary get(int id) {
        if (id < 0 || words.size() <= id) {
            logger.warning( "There is no word with that id: " + id);
            return new Vocabulary(-1, "");
        }
        return words.get(id);
    }
    
    List<Vocabulary> getWords() {
        if(Objects.isNull(words)){
            logger.warning( "Words were not initialized");
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(words);
    }
}
