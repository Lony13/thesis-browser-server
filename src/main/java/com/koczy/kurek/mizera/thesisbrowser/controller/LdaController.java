package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.model.CompareThesesDto;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import com.koczy.kurek.mizera.thesisbrowser.service.DemoServiceResolver;
import com.koczy.kurek.mizera.thesisbrowser.service.ILdaService;
import com.koczy.kurek.mizera.thesisbrowser.service.LdaDemoService;
import com.koczy.kurek.mizera.thesisbrowser.service.LdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LdaController extends DemoServiceResolver<ILdaService> {

    @Autowired
    public LdaController(LdaService ldaService, LdaDemoService ldaDemoService) {
        super(ldaService, ldaDemoService);
    }

    @RequestMapping(value="/api/lda/run", method = RequestMethod.POST)
    public ResponseEntity<ServerInfo> run(HttpServletRequest request){
        return resolveService(request).run();
    }

    @RequestMapping(value = "/api/theses/similar", method = RequestMethod.GET)
    public ResponseEntity<List<ThesisResponse>> getSimilarThesesAmongFromFilter(@RequestBody ThesisFilters thesisFilters,
                                                                                HttpServletRequest request) {
        return resolveService(request).getSimilarThesesFromFilter(thesisFilters);
    }

    @RequestMapping(value = "/api/similarity/{id1}/{id2}", method = RequestMethod.GET)
    public ResponseEntity<Double> getSimilarity(@PathVariable(value = "id1") int id1,
                                                 @PathVariable(value = "id2") int id2,
                                                HttpServletRequest request) {
        return resolveService(request).getSimilarity(id1, id2);
    }

    @Deprecated
    @RequestMapping(value = "/api/theses/similar/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ThesisResponse>> getSimilarTheses(@PathVariable(value = "id") int id,
                                                                 HttpServletRequest request) {
        return resolveService(request).getSimilarTheses(id);
    }

    @Deprecated
    @RequestMapping(value = "/api/similarThesesAmong", method = RequestMethod.POST)
    public ResponseEntity<List<Integer>> getSimilarThesesAmong(@RequestBody CompareThesesDto compareThesesDto,
                                                               HttpServletRequest request) {
        return resolveService(request).getSimilarThesesAmong(compareThesesDto);
    }
}
