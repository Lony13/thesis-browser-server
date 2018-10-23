package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.lda.dataset.Dataset;
import com.koczy.kurek.mizera.thesisbrowser.lda.lda.LDA;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LdaService implements ILdaService{

    private Dataset dataset;
    private LDA lda;

    @Autowired
    public LdaService(Dataset dataset, LDA lda){
        this.dataset = dataset;
        this.lda = lda;
    }

    @Override
    public ResponseEntity run() {
        this.lda.run();
        System.out.println(this.lda.computePerplexity(this.dataset));

        for (int t = 0; t < lda.getNumTopics(); ++t) {
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
