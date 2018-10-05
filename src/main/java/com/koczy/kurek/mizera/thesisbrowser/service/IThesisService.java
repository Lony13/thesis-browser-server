package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;

import java.util.List;

public interface IThesisService {

    List<Thesis> getTheses();

    List<Thesis> getThesesByAuthor(String author);

    List<Thesis> getThesesByTitle(String title);

}
