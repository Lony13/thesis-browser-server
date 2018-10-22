package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Dataset;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.koczy.kurek.mizera.thesisbrowser.lda.lda.inference.InferenceMethod.CGS;

import java.util.List;


@Service
public class LdaService implements ILdaService{

    public LdaService(){}

    @Override
    public ResponseEntity run() {
        Dataset dataset = new Dataset("src/main/resources/vocab.kos.txt");
        final int numTopics = 10;
        LDA lda = new LDA(0.1, 0.1, numTopics, dataset, CGS);
        lda.run();
        System.out.println(lda.computePerplexity(dataset));

        for (int t = 0; t < numTopics; ++t) {
            List<Pair<String, Double>> highRankVocabs = lda.getVocabsSortedByPhi(t);
            System.out.print("t" + t + ": ");
            for (int i = 0; i < 5; ++i) {
                System.out.print("[" + highRankVocabs.get(i).getLeft() + "," + highRankVocabs.get(i).getRight() + "],");
            }
            System.out.println();
        }

        return new ResponseEntity<>("LDA completed", HttpStatus.OK);
    }
}
