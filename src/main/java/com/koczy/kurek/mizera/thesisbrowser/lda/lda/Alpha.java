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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class Alpha {
    private List<Double> alphas;
    private IAuthorDao authorDao;

    @Autowired
    Alpha(@Value("${lda.alpha}") double alpha, @Value("${lda.numTopics}") int numTopics, IAuthorDao authorDao) {
        this.authorDao = authorDao;
        numTopics = (numTopics == 0)?authorDao.getAuthorsNum():numTopics;
        this.alphas = Stream.generate(() -> alpha)
                            .limit(numTopics)
                            .collect(Collectors.toList());
    }

    double get(int i) {
        return alphas.get(i);
    }
    
    void set(int i, double value) {
        alphas.set(i, value);
    }

    double sum() {
        return alphas.stream().reduce(Double::sum).get();
    }

    public List<Double> getAlphas() {
        return alphas;
    }
}
