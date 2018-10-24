package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.service.ILdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LdaController {


    private ILdaService ldaService;

    @Autowired
    public LdaController(ILdaService ldaService) {
        this.ldaService = ldaService;
    }

    @RequestMapping(value="/api/lda/run", method = RequestMethod.POST)
    public ResponseEntity run(){
        return ldaService.run();
    }

    @RequestMapping(value = "/api/similarity/{id1}/{id2}", method = RequestMethod.GET)
    public ResponseEntity<Double> getSimilarity(@PathVariable(value = "id1") int id1,
                                                 @PathVariable(value = "id2") int id2) {
        return ldaService.getSimilarity(id1, id2);
    }

    @RequestMapping(value = "/api/similarTheses/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Thesis>> getSimilarTheses(@PathVariable(value = "id") int id) {
        return ldaService.getSimilarTheses(id);
    }
    @RequestMapping(value = "/api/similarThesesAmong/{id}", method = RequestMethod.POST)
    public ResponseEntity<List<Integer>> getSimilarThesesAmong(@PathVariable(value = "id") int id,
                                                         @RequestBody List<Integer> theses) {
        return ldaService.getSimilarThesesAmong(id, theses);
    }
}
