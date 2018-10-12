package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.service.IDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DownloadController {

    private IDownloadService downloadService;

    @Autowired
    public DownloadController(IDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @RequestMapping(value = "/api/theses/download", method = RequestMethod.GET)
    public ResponseEntity downloadTheses() {
        return downloadService.downloadTheses();
    }
}
