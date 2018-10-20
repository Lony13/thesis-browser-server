package com.koczy.kurek.mizera.thesisbrowser.hibUtils;

import com.koczy.kurek.mizera.thesisbrowser.entity.Author;
import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThesisDAO {

    private static final Logger logger = Logger.getLogger(ThesisDAO.class.getName());

    //TODO add filter over position in authors list
    public static List<Thesis> searchTheses(ThesisFilters thesisFilters) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<Thesis> thesisList;

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

        //filter with date
        if(Objects.nonNull(thesisFilters.getDateFrom())){
            thesisList.removeIf(thesis -> thesis.getPublicationDate().after(thesisFilters.getDateFrom()));
        }
        if(Objects.nonNull(thesisFilters.getDateTo())){
            thesisList.removeIf(thesis -> thesis.getPublicationDate().after(thesisFilters.getDateTo()));
        }
        session.close();

        return thesisList;
    }

    private static String createQuery(ThesisFilters filters) {

        String query = "SELECT * FROM thesis \n" +
                "JOIN author_thesis on thesis.thesisId = author_thesis.thesisId \n" +
                "JOIN author on author_thesis.authorId = author.authorId \n" +
                "JOIN keywords on keywords.thesisId = thesis.thesisId \n" +
                "WHERE ";
        Boolean addAnd = false;

        if (!isBlank(filters.getTitle())) {
            query = addAddToWhere(addAnd, query);
            query = query.concat("thesis.title LIKE '%" + filters.getTitle() + "%' ");
            addAnd = true;
        }
        if (!isBlank(filters.getAuthor())) {
            query = addAddToWhere(addAnd, query);
            Author author = AuthorDAO.getAuthorByName(filters.getAuthor());
            query = query.concat("author_thesis.authorId = " + author.getAuthorId() + " ");
            addAnd = true;
        }
        if (!isBlank(filters.getInstitution())) {
            query = addAddToWhere(addAnd, query);
            query = query.concat("author.university LIKE '%" + filters.getInstitution() + "%' ");
            addAnd = true;
        }
        if (!isBlank(filters.getKeyWords())) {
            query = addAddToWhere(addAnd, query);
            query = query.concat("keywords.keyWords LIKE '%" + filters.getKeyWords() + "%' ");
            addAnd = true;
        }
        if (!isBlank(filters.getQuotationNumber())) {
            query = addAddToWhere(addAnd, query);
            query = query.concat("thesis.citationNo = " + filters.getQuotationNumber() + " ");
            addAnd = true;
        }

        return query;
    }

    private static Boolean isBlank(String filter) {
        return Objects.isNull(filter) || filter.trim().isEmpty();
    }

    private static Boolean isBlank(Integer filter) {
        return Objects.isNull(filter) || filter < 0;
    }

    private static String addAddToWhere(Boolean addAnd, String query) {
        return (addAnd ? query.concat("AND ") : query);
    }
}
