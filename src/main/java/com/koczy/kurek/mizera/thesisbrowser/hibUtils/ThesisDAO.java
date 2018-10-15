package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class ThesisDAO {


    public static List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Thesis> thesisList = new ArrayList<>();

        try {
            String sqlQuery = createQuery(thesisFilters);
            thesisList = session.createNativeQuery(sqlQuery, Thesis.class).list();
            for (Thesis thesis : thesisList) {
                Hibernate.initialize(thesis.getRelatedTheses());
                Hibernate.initialize(thesis.getKeyWords());
                Hibernate.initialize(thesis.getAuthors());
            }
        }catch (NullPointerException e){
            thesisList = new ArrayList<>();
        }

        session.close();

        return thesisList;
    }

    private static String createQuery(ThesisFilters filters) {
        String query = "SELECT * FROM thesis WHERE ";

        if (filters.getTitle() != null) {
            query = query.concat("title LIKE '%" + filters.getTitle() + "%'");
        }
        //TODO add rest filters

        return query;
    }
}
