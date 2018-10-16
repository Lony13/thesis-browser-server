package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.entity.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;

import java.util.List;

public interface IThesisService {

    List<Thesis> getTheses();

    List<Thesis> searchTheses(ThesisFilters thesisFilters);

    ThesisDetails getThesisDetails(Long id);

}
