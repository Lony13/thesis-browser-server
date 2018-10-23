package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Dataset;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.koczy.kurek.mizera.thesisbrowser.model.Constants.LDA_NUM_TOPICS;


@Service
public class LdaService implements ILdaService{

    public LdaService(){}

    @Override
    public ResponseEntity run() {
        Dataset dataset = new Dataset("src/main/resources/vocab.kos.txt");
        LDA lda = new LDA(0.1, 0.1, LDA_NUM_TOPICS, dataset);
        lda.run();
        System.out.println(lda.computePerplexity(dataset));

        for (int t = 0; t < LDA_NUM_TOPICS; ++t) {
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
