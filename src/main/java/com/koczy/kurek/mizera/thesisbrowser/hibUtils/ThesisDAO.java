package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThesisDAO {

    private static final Logger logger = Logger.getLogger(ThesisDAO.class.getName());

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
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "NullPointerException in thesisFilers. Returning empty list." + e.toString());
            e.printStackTrace();
            thesisList = new ArrayList<>();
        }

        session.close();

        return thesisList;
    }

    private static String createQuery(ThesisFilters filters) {
        String query = "SELECT * FROM thesis WHERE ";

        if (filters.getTitle() != null && !filters.getTitle().isEmpty()) {
            query = query.concat("title LIKE '%" + filters.getTitle() + "%'" );
        }
        //TODO add rest filters

        return query;
    }
}
