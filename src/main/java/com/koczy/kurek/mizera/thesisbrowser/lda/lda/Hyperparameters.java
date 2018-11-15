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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
class Hyperparameters {
    private static final Logger logger = Logger.getLogger(Hyperparameters.class.getName());

    private Alpha alpha;
    private Beta beta;

    @Autowired
    Hyperparameters(Alpha alpha, Beta beta) {
        this.alpha = alpha;
        this.beta  = beta;
    }

    double alpha(int i) {
        if(alpha.getAlphas().size() - 1 < i){
            logger.warning("Given i value is to big, i: " + i);
            return -1;
        }
        return alpha.get(i);
    }
    
    double sumAlpha() {
        return alpha.sum();
    }
    
    double beta() {
        return beta.get();
    }
    
    double beta(int i) {
        if(beta.getBetas().size() - 1 < i){
            logger.warning("Given i value is to big, i: " + i);
            return -1;
        }
        return beta.get(i);
    }
    
    void setAlpha(int i, double value) {
        alpha.set(i, value);
    }
    
    void setBeta(int i, double value) {
        beta.set(i, value);
    }
    
    void setBeta(double value) {
        beta.set(0, value);
    }
}
