package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.model.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import com.koczy.kurek.mizera.thesisbrowser.service.DemoServiceResolver;
import com.koczy.kurek.mizera.thesisbrowser.service.IThesisService;
import com.koczy.kurek.mizera.thesisbrowser.service.ThesisDemoService;
import com.koczy.kurek.mizera.thesisbrowser.service.ThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ThesisController extends DemoServiceResolver<IThesisService> {

    @Autowired
    public ThesisController(ThesisService thesisService, ThesisDemoService thesisDemoService) {
        super(thesisService, thesisDemoService);
    }

    @RequestMapping(value = "/api/theses", method = RequestMethod.GET)
    public ResponseEntity<List<ThesisResponse>> getThesis(HttpServletRequest request) {
        return new ResponseEntity<>(resolveService(request).getTheses(), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/details/{id}", method = RequestMethod.GET)
    public ResponseEntity<ThesisDetails> getThesisDetails(@PathVariable(value = "id") int id,
                                                          HttpServletRequest request) {
        return new ResponseEntity<>(resolveService(request).getThesisDetails(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/search", method = RequestMethod.POST)
    public ResponseEntity<List<ThesisResponse>> searchTheses(@RequestBody(required = false) ThesisFilters thesisFilters,
                                                             HttpServletRequest request) {
        return new ResponseEntity<>(resolveService(request).searchTheses(thesisFilters), HttpStatus.OK);
    }

}




