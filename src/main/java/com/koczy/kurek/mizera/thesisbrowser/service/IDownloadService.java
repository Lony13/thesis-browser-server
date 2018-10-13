package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.http.ResponseEntity;

public interface IDownloadService {

    ResponseEntity downloadTheses(ThesisFilters thesisFilters);

}
