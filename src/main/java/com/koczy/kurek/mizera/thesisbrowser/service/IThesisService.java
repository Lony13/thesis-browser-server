package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.model.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;

import java.util.List;

public interface IThesisService {

    List<ThesisResponse> getTheses();

    List<ThesisResponse> searchTheses(ThesisFilters thesisFilters);

    ThesisDetails getThesisDetails(int id);

    ThesisResponse getThesis(int id);
}
