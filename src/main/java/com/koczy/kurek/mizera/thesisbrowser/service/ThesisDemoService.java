package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.entity.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThesisDemoService implements IThesisService {

    private ArrayList<Thesis> theses = new ArrayList<Thesis>(){{
        add(new Thesis(1, "How hard is control in single-crossing elections?", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf"));
        add(new Thesis(2, "Multiwinner Elections With Diversity Constraints", "Piotr Faliszewski", "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777"));
        add(new Thesis(3, "Properties of multiwinner voting rules.", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf"));
        add(new Thesis(4, "Semantic Text Indexing.", "Zbigniew Kaleta", "https://journals.agh.edu.pl/csci/article/view/148/810"));
        add(new Thesis(5, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.", "Roman Dębski", "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n"));
    }};

    private ArrayList<ThesisDetails> thesesDetails = new ArrayList<ThesisDetails>(){{
        add(new ThesisDetails(1, "How hard is control in single-crossing elections?", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf", 4));
        add(new ThesisDetails(2, "Multiwinner Elections With Diversity Constraints", "Piotr Faliszewski", "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777", 6));
        add(new ThesisDetails(3, "Properties of multiwinner voting rules.", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf", 0));
        add(new ThesisDetails(4, "Semantic Text Indexing.", "Zbigniew Kaleta", "https://journals.agh.edu.pl/csci/article/view/148/810", 4));
        add(new ThesisDetails(5, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.", "Roman Dębski", "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n", 15));
    }};

    @Override
    public List<Thesis> getTheses() {
        return theses;
    }

    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        List<Thesis> searchedTheses = new ArrayList<>(theses);
        searchedTheses.removeIf(thesis -> !thesis.getAuthor().toLowerCase().contains(thesisFilters.getAuthor().toLowerCase()));
        return searchedTheses;
    }

    @Override
    public ThesisDetails getThesisDetails(Long id) {
        for (ThesisDetails thesisDetails : thesesDetails) {
            if(thesisDetails.getId() == id)
                return thesisDetails;
        }
        return new ThesisDetails();
    }
}
