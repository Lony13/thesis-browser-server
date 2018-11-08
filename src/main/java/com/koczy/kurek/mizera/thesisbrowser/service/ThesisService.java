package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThesisService implements IThesisService {

    private IThesisDao thesisDao;

    @Autowired
    public ThesisService(IThesisDao thesisDao) {
        this.thesisDao = thesisDao;
    }

    @Override
    public List<ThesisResponse> getTheses() {
        return Collections.emptyList();
    }

    @Override
    public List<ThesisResponse> searchTheses(ThesisFilters thesisFilters) {
        List<Thesis> theses = thesisDao.searchTheses(thesisFilters);
        return theses.stream()
                .map(ThesisResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public ThesisDetails getThesisDetails(int id) {
        Thesis thesis = thesisDao.getThesis(id);
        return new ThesisDetails(thesis);
    }

    @Override
    public ThesisResponse getThesis(int id) {
        return new ThesisResponse();
    }
}
