package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.service.IThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ThesisController {

    private IThesisService thesisService;

    @Autowired
    public ThesisController(IThesisService thesisService) {
        this.thesisService = thesisService;
    }

    @RequestMapping(value = "/api/theses", method = RequestMethod.GET)
    public ResponseEntity<List<Thesis>> getThesis() {
        List<Thesis> theses = thesisService.getTheses();
        return new ResponseEntity<>(theses, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/search", method = RequestMethod.GET)
    public ResponseEntity<List<Thesis>> searchTheses(@RequestBody(required = false) ThesisFilters thesisFilters) {
        List<Thesis> theses = thesisService.searchTheses(thesisFilters);
        return new ResponseEntity<>(theses, HttpStatus.OK);
    }

}




