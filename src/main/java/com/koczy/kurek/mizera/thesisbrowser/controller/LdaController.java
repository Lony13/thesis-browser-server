package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.service.ILdaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
