package com.koczy.kurek.mizera.thesisbrowser.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ThesisController {

    @RequestMapping(value = "/api/theses", method = RequestMethod.GET)
    public String getThesis(){
        return "test";
    }
}




