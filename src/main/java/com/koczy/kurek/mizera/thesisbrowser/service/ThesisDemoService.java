package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.entity.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ThesisDemoService implements IThesisService {

    private ArrayList<ThesisResponse> theses = new ArrayList<ThesisResponse>() {{
        add(new ThesisResponse(0, "How hard is control in single-crossing elections?",
                "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski"))));
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

    private ArrayList<ThesisDetails> thesesDetails = new ArrayList<ThesisDetails>() {{
        add(new ThesisDetails(0, "How hard is control in single-crossing elections?",
                "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski")), 5, new Date(), null,
                new HashSet<>(Arrays.asList("test", "demo", "words"))));
        add(new ThesisDetails(1, "Multiwinner Elections With Diversity Constraints",
                "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski")), 5, new Date(), null,
                new HashSet<>(Arrays.asList("test", "demo", "words"))));
        add(new ThesisDetails(2, "Properties of multiwinner voting rules.",
                "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf",
                new ArrayList<>(Collections.singletonList("Piotr Faliszewski")), 5, new Date(), null,
                new HashSet<>(Arrays.asList("test", "demo", "words"))));
        add(new ThesisDetails(3, "Semantic Text Indexing.",
                "https://journals.agh.edu.pl/csci/article/view/148/810",
                new ArrayList<>(Collections.singletonList("Zbigniew Kaleta")), 5, new Date(), null,
                new HashSet<>(Arrays.asList("test", "demo", "words"))));
        add(new ThesisDetails(4, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.",
                "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n",
                new ArrayList<>(Collections.singletonList("Roman Dębski")), 5, new Date(), null,
                new HashSet<>(Arrays.asList("test", "demo", "words"))));
    }};

    @Override
    public List<ThesisResponse> getTheses() {
        return theses;
    }

    @Override
    public List<ThesisResponse> searchTheses(ThesisFilters thesisFilters) {
        List<ThesisResponse> searchedTheses = new ArrayList<>(theses);
        searchedTheses.removeIf(thesis -> !thesis.getAuthors().contains(thesisFilters.getAuthor().toLowerCase()));
        return searchedTheses;
    }

    @Override
    public ThesisDetails getThesisDetails(int id) {
        if (this.thesesDetails.size() > id) {
            return this.thesesDetails.get(id);
        }
        return this.thesesDetails.get(0);
    }

    @Override
    public Thesis getThesis(int id) {
        return theses.get(id);
    }
}
