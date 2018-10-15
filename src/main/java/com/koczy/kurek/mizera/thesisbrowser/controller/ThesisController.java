package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.service.DemoServiceResolver;
import com.koczy.kurek.mizera.thesisbrowser.service.IThesisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ThesisController extends DemoServiceResolver<IThesisService> {

    private IThesisService thesisService;
    private IThesisService thesisDemoService;

    @Autowired
    public ThesisController(IThesisService thesisService, IThesisService thesisDemoService) {
        super(thesisService, thesisDemoService);
        this.thesisService = thesisService;
        this.thesisDemoService = thesisDemoService;
    }

    @RequestMapping(value = "/api/theses", method = RequestMethod.GET)
    public ResponseEntity<List<Thesis>> getThesis(@RequestParam String role, HttpServletRequest request) {
        List<Thesis> theses = resolveService(role).getTheses();
        return new ResponseEntity<>(theses, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/theses/search", method = RequestMethod.GET)
    public ResponseEntity<List<Thesis>> searchTheses(@RequestBody(required = false) ThesisFilters thesisFilters) {
        List<Thesis> theses = thesisService.searchTheses(thesisFilters);
        return new ResponseEntity<>(theses, HttpStatus.OK);
    }

}




