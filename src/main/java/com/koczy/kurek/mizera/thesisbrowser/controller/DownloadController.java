package com.koczy.kurek.mizera.thesisbrowser.controller;

import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.service.IDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DownloadController {

    private IDownloadService downloadService;

    @Autowired
    public DownloadController(IDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @RequestMapping(value = "/theses/download", method = RequestMethod.POST)
    public ResponseEntity<ServerInfo> downloadTheses(@RequestBody ThesisFilters thesisFilters) {
        return downloadService.downloadTheses(thesisFilters);
    }

    @RequestMapping(value = "/api/thesis/quotation/updates", method = RequestMethod.GET)
    public ResponseEntity<ServerInfo> updateQuotations(@RequestParam int thesisId) {
        return downloadService.updateQuotations(thesisId);
    }
}
