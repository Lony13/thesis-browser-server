package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.CompareThesesDto;
import com.koczy.kurek.mizera.thesisbrowser.model.ServerInfo;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ILdaService {

    ResponseEntity<ServerInfo> run();

    ResponseEntity<Double> getSimilarity(int id1, int id2);

    ResponseEntity<List<ThesisResponse>> getSimilarTheses(int id);

    ResponseEntity<List<Integer>> getSimilarThesesAmong(CompareThesesDto compareThesesDto);

    ResponseEntity<List<ThesisResponse>> getSimilarThesesFromFilter(ThesisFilters thesisFilters);
}
