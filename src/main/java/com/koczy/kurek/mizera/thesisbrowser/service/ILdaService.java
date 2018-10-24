package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ILdaService {

    ResponseEntity run();

    ResponseEntity<Double> getSimilarity(int id1, int id2);

    ResponseEntity<List<Thesis>> getSimilarTheses(int id);

    ResponseEntity<List<Integer>> getSimilarThesesAmong(int id, List<Integer> theses);
}
