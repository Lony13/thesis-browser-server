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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class Vocabularies {
    private static final Logger logger = Logger.getLogger(Vocabularies.class.getName());

    private List<Vocabulary> vocabs;

    @Autowired
    public Vocabularies(@Value("${lda.vocabs}") String filePath) {
        try {
            Path vocabFilePath = Paths.get(filePath);
            List<String> lines = Files.readAllLines(vocabFilePath);
            vocabs = lines.stream().map(v -> new Vocabulary(lines.indexOf(v) + 1, v))
                                   .collect(Collectors.toList());
        } catch (IOException ioe) {
            logger.warning( "Couldn't read lines from vocab file, filePath: " + filePath);
        }
    }
    
    public Vocabulary get(int id) {
        return vocabs.get(id - 1);
    }
    
    public int size() {
        return vocabs.size();
    }
    
    public List<Vocabulary> getVocabularyList() {
        return Collections.unmodifiableList(vocabs);
    }
}
