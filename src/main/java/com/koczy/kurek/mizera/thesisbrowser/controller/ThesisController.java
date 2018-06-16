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
        theses.add(new Thesis(1, "My topic of work", "MR. Falisz", "http://google.com"));
        theses.add(new Thesis(2, "Second work topic", "John Nice", "http://google.pl"));
        theses.add(new Thesis(3, "This is super thesis", "John Nice", "http://google.pl"));
        theses.add(new Thesis(4, "Whats up guys?", "Ben White", "http://google.pl"));

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




