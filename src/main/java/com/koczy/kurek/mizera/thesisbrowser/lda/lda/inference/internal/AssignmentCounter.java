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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class AssignmentCounter {
    private static final Logger logger = Logger.getLogger(AssignmentCounter.class.getName());

    private List<Integer> counter;

    AssignmentCounter(int size) {
        this.counter = new ArrayList<>();
        if (size <= 0) {
            logger.warning( "Size of counter can't be smaller than 1");
            return;
        }
        this.counter = IntStream.generate(() -> 0)
                                .limit(size)
                                .boxed()
                                .collect(Collectors.toList());
    }
    
    int size() {
        return counter.size();
    }
    
    int get(int id) {
        if(id < 0 || counter.size() <= id) {
            logger.warning( "There is no such id");
            return -1;
        }
        return counter.get(id);
    }
    
    int getSum() {
        return counter.stream().reduce(Integer::sum).get();
    }
    
    void increment(int id) {
        if (id < 0 || counter.size() <= id) {
            logger.warning( "There is no such id");
            return;
        }
        counter.set(id, counter.get(id) + 1);
    }
    
    void decrement(int id) {
        if (id < 0 || counter.size() <= id ) {
            logger.warning( "There is no such id");
            return;
        }
        if(counter.get(id) == 0) {
            logger.warning( "There is no possibility to decrement counter lower than 0");
            return;
        }
        counter.set(id, counter.get(id) - 1);
    }
}
