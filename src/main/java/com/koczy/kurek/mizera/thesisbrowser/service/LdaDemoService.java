package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LdaDemoService implements ILdaService{

    private ArrayList<ThesisResponse> similarTheses = new ArrayList<ThesisResponse>() {{
        add(new ThesisResponse(0, "How hard is control in single-crossing elections?",
                "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf",
                new ArrayList<>(Arrays.asList("Piotr Faliszewski", "Zbigniew Kaleta"))));
        add(new ThesisResponse(1, "Multiwinner Elections With Diversity Constraints",
                "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski"))));
        add(new ThesisResponse(2, "Properties of multiwinner voting rules.",
                "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski"))));
        add(new ThesisResponse(3, "Semantic Text Indexing.",
                "https://journals.agh.edu.pl/csci/article/view/148/810",
                new ArrayList<>(Collections.singletonList("Zbigniew Kaleta"))));
        add(new ThesisResponse(4, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.",
                "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n",
                new ArrayList<>(Collections.singletonList("Roman Dębski"))));
    }};


    @Override
    public ResponseEntity<ServerInfo> run() {
        return new ResponseEntity<>(new ServerInfo(new Date(), "LDA completed"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getSimilarity(int id1, int id2) {
        return new ResponseEntity<>(0d, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ThesisResponse>> getSimilarTheses(int id) {
        return new ResponseEntity<>(similarTheses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Integer>> getSimilarThesesAmong(CompareThesesDto compareThesesDto) {
        return new ResponseEntity<>(compareThesesDto.getThesesToCompare(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ThesisResponse>> getSimilarThesesFromFilter(ExemplaryThesesDto exemplaryTheses, ThesisFilters thesisFilters) {
        return new ResponseEntity<>(similarTheses, HttpStatus.OK);
    }
}
