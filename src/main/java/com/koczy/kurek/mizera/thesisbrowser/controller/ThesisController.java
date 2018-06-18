package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class ThesisController {

    private ArrayList<Thesis> theses = new ArrayList<>();

    @RequestMapping(value = "/api/theses", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Thesis>> getThesis() {
        theses = new ArrayList<>();
        theses.add(new Thesis(1, "How hard is control in single-crossing elections?", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs10458-016-9339-3.pdf"));
        theses.add(new Thesis(2, "Multiwinner Elections With Diversity Constraints", "Piotr Faliszewski", "https://www.aaai.org/ocs/index.php/AAAI/AAAI18/paper/view/16769/15777"));
        theses.add(new Thesis(3, "Properties of multiwinner voting rules.", "Piotr Faliszewski", "https://link.springer.com/content/pdf/10.1007%2Fs00355-017-1026-z.pdf"));
        theses.add(new Thesis(4, "Semantic Text Indexing.", "Zbigniew Kaleta", "https://journals.agh.edu.pl/csci/article/view/148/810"));
        theses.add(new Thesis(5, "Classic and Agent-Based Evolutionary Heuristics for Shape Optimization of Rotating Discs.", "Roman Dębski", "http://www.cai.sk/ojs/index.php/cai/article/view/2017_2_331/823\n"));

        return new ResponseEntity<>(theses, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/author", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Thesis>> getThesisByAuthor(@RequestParam String author) {
        theses.removeIf(thesis -> !thesis.getAuthor().toLowerCase().contains(author.toLowerCase()));
            return new ResponseEntity<>(theses, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/title", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Thesis>> getThesisByTitle(@RequestParam String title) {
        theses.removeIf(thesis -> !thesis.getTitle().toLowerCase().contains(title.toLowerCase()));
        return new ResponseEntity<>(theses, HttpStatus.OK);
    }
}




