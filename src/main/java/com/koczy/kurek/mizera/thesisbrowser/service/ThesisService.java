package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.entity.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.IThesisDao;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ThesisService implements IThesisService {

    private IThesisDao thesisDao;

    @Autowired
    public ThesisService(IThesisDao thesisDao) {
        this.thesisDao = thesisDao;
    }

    @Override
    public List<Thesis> getTheses() {
        return Collections.emptyList();
    }

    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        return thesisDao.searchTheses(thesisFilters);
    }

    @Override
    public ThesisDetails getThesisDetails(int id) {
        return new ThesisDetails();
    }
}
