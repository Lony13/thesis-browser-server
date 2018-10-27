package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;

import java.util.List;

public interface IThesisDao {

    List<Thesis> searchTheses(ThesisFilters thesisFilters);

}
