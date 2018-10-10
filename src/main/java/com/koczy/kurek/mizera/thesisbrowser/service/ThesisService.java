package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThesisService implements IThesisService {

    private ArrayList<Thesis> theses = new ArrayList<>();

    @Override
    public List<Thesis> getTheses() {
        prepareTheses();
        return theses;
    }

    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        prepareTheses();
        theses.removeIf(thesis -> !thesis.getAuthor().toLowerCase().contains(thesisFilters.getAuthor().toLowerCase()));
        return theses;
    }

    private void prepareTheses() {
        theses.clear();
        theses.add(new Thesis(1, "How hard is control in single-crossing elections?", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf"));
        theses.add(new Thesis(2, "Multiwinner Elections With Diversity Constraints", "Piotr Faliszewski", "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777"));
        theses.add(new Thesis(3, "Properties of multiwinner voting rules.", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf"));
        theses.add(new Thesis(4, "Semantic Text Indexing.", "Zbigniew Kaleta", "https://journals.agh.edu.pl/csci/article/view/148/810"));
        theses.add(new Thesis(5, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.", "Roman Dębski", "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n"));
    }
}
