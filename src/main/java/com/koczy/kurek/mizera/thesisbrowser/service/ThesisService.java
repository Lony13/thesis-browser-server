package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.HibernateUtil;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ThesisService implements IThesisService {

    @Override
    public List<Thesis> getTheses() {
        return Collections.emptyList();
    }

    @Override
    public List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        String sqlQuery = createQuery(thesisFilters);
        List<Thesis> thesisList = session.createNativeQuery(sqlQuery, Thesis.class).list();
        return Collections.emptyList();
    }

    private String createQuery(ThesisFilters filters) {
        String query = "SELECT * FROM thesis WHERE ";

        if (filters.getTitle() != null) {
            query = query.concat("title LIKE '%" + filters.getTitle() + "%'");
        }
        //TODO add rest filters
        return query;
    }

}
